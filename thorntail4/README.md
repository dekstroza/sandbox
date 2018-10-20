# Thorntail 4 Example

Basic Thorntail 4 example with Cassandra Extension. Project has two modules:
1. repository-cdi-extension
2. thorntail4-demo-app

First module contains the source code of the extension, and second module contains
demo application using it. Both can be built with either JDK 8 or JDK 11. If using JDK 11 tests will be skipped (keep reading to se why). 

Demo application can be run by simply running ```GameService``` from IDE with run, or from the command line (after successful build) with:
```bash
cd  target
./thorntail4-demo-app-1.0.1-SNAPSHOT-bin/bin/run.sh

```
Similarly, if using JDK 8, tests can be executed from IDE, by just running them as any other junit test. Demo application also contains k8s folder with helm chart for running demo application on kubernetes, this will require Cassandra deployed and reachable on kubernetes, and ```values.yaml``` file has to be adjusted to point to cassandra nodes and port.

Docker container is built using alpine image, customised with JDK 11 reduced down to only modules used by the demo app, producing very small container size. This is achieved using jdeps/jlink and multistage docker build.

```bash
REPOSITORY                                               TAG                 IMAGE ID            CREATED             SIZE
dekstroza/thorntail4-app                                 1.0.1-SNAPSHOT      306d7db6b952        10 hours ago        83.6MB
```
 

## Requirements

1. OpenJDK 11 (no tests) or OpenJDK 8 (if running tests, see bellow)
2. Maven 3.5.3 or greater
3. Docker (if building container)

## Build instructions

To build without running tests use JDK 11, build will automatically disable them. Cassandra does not support
JDK 11, so test cases will fail when running embedded cassandra with cassandra-unit.
Docker container will be built automatically when project is built with:

```
./mvnw clean install
```
To disable docker build run
```
./mvnw clean install -Ddockerfile.skip
```
or alternatively, if you would like to completely disable docker build, set property ```dockerfile.skip``` to ```true``` inside ```thorntail4-demo-app/pom.xml```.
When building docker image, please adjust property: 
```
<docker.repository>dekstroza/thorntail4-app</docker.repository>
```
in demo application pom file, so that it points to docker repository to which you have access. Format is repository/image-name, and tag will be used from project version.
Same information has to be provided in k8s helm chart, adjusting ```values.yaml``` file and corresponding settings.
```yaml
image: dekstroza/thorntail4-app
tag: 1.0.1-SNAPSHOT
pullPolicy: Always
cassandraClusterName: myCluster
cassandraContactPoints: cassandra-node
cassandraPort: 9042

```

## Running Test Cases
When JDK 8 is used, testcases will be executed as well, otherwise if jdk11 is used, they will be skipped. Reason for this is problem with running Cassandra on JDK 11, and some of the test cases use cassandra-unit which starts embedded Cassandra on jvm used by mvn itself. Tests can be run like any other junit.

## Using Extension
To use extension with Thorntail 4 add dependency into pom.xml 
```xml
<dependency>
  <groupId>io.dekstroza</groupId>
  <artifactId>repository-cdi-extension</artifactId>
  <version>1.0.1-SNAPSHOT</version>
</dependency>
``` 
Annotate class containing main method with ```@EnableCassandraRepository``` (this will activate extension), otherwise no injection will happend, nor it will try
to connect to Cassandra cluster. Annotation has additional property called ```create``` which is set by default to true, and will cause extension to create keyspaces and tables for annotated classes found. 
Example:
```java
@EnableCassandraRepository(create=false)
@ApplicationPath("/")
public class GameService extends Application {
  public static void main(String... args) throws Exception {
    Thorntail.run();
  }
}
```
Create your model classes and annotate them using cassandra driver annotations, 
see: [https://docs.datastax.com](https://docs.datastax.com/en/developer/java-driver/3.2/manual/object_mapper/) for more details.

For example:
```java
package io.dekstroza.model;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Table(
    keyspace = "ks1",
    name = "games1",
    readConsistency = "QUORUM",
    writeConsistency = "QUORUM",
    caseSensitiveKeyspace = false,
    caseSensitiveTable = false)
public class Game {

  @PartitionKey
  @Column(name = "id")
  private UUID id;

  @Column(name = "bandName")
  private String bandName;

  @Column(name = "track")
  private String track;

  @Column(name = "bandMembers")
  private List<String> bandMembers;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getBandName() {
    return bandName;
  }

  public void setBandName(String bandName) {
    this.bandName = bandName;
  }

  public String getTrack() {
    return track;
  }

  public void setTrack(String track) {
    this.track = track;
  }

  public List<String> getBandMembers() {
    return bandMembers;
  }

  public void setBandMembers(List<String> bandMembers) {
    this.bandMembers = bandMembers;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Game game = (Game) o;
    return Objects.equals(getId(), game.getId())
        && Objects.equals(getBandName(), game.getBandName())
        && Objects.equals(getTrack(), game.getTrack())
        && Objects.equals(getBandMembers(), game.getBandMembers());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getBandName(), getTrack(), getBandMembers());
  }
}
```

Once all model classes are annotated, create your repository bean (can be application scope or request scope)
and add repository field, marking entity type and type of cassandra partition key used for it. In the example bellow
Game is entity type, and it's partition key will be UUID type.
```java
@ApplicationScoped
@Transactional
public class GameResource {

  @Repository private CrudRepository<Game, UUID> repository;
}
```
or if not using application scoped bean:
```java
@RequestScope
@Transactional
public class GameResource {

  @Repository private CrudRepository<Game, UUID> repository;
}
  
 
```

Interface ```CrudRepository``` offers both sync and async methods to delete, read, update and delete entities. See javdocs for more information on the available methods. Use the provided CrudRepository to create, read, update and delete objects in Cassandra.

####Keyspace and Tables Creation
If not specified on ```@EnableCassandraRepository``` extension will create keyspaces and tables for all annotated classes. This is limited just to keyspace(s) and tables, and very simplistic for start. 
For more complicated cases, disable creation by specifying ```@EnableCassandraRepository(create=false)``` and create all the keyspace(s) and tables manually.

#### Configuring Cassandra Connection
Cassandra configuration is specified using micro-profile config, so there are multiple ways to do it.

- Create application.properties file and place it into META-INF folder:
```bash
# Cluster name
cassandra.cluster.name=myCluster
# Coma separated list of cassandra nodes
cassandra.contact.points=127.0.0.1,192.168.1.1
# Port to connect to
cassandra.port=9042
```
or
- Export ENV variables:
```bash
export CASSANDRA_CLUSTER_NAME=myCluster
export CASSANDRA_CONTACT_POINTS="127.0.0.1,192.168.1.1"
export CASSANDRA_PORT=9042
```
or
- Specify above as JVM arguments:
```bash
-Dcassandra.cluster.name=myCluster \
-Dcassandra.contact.points="127.0.0.1,192.168.1.1" \
-Dcassandra.port=9042
```
or use the other ways to configure these, for details see: [https://docs.thorntail.io](https://docs.thorntail.io/4.0.0-SNAPSHOT/#_configuration)

## Limitations

Available types mapped to Cassandra table types are limited to basic java types + Set,Map and List. More to come soon.
Custom type definitions and accessors are not yet supported.
