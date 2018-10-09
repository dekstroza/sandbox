# Custom thorntatil4 branch project archetype

## Requirements
Project generated with this maven archetype will require following:

1. Java 11
2. Maven to generate project from archetype
3. Up to date Docker (optional, see building the project)

## Instructions

Clone project and install archetype with:
```
mvn clean install
```
## Generate projects

Use the archetype to generate projects. All properties have default values.
```
# Sets docker repo by default to this value, can be overriden when generating project
# by default docker repo will be set to dekstroza

dockerRepo=myDockerRepo 
```
To generate project use usual maven archetype commands, for example:
```
mvn archetype:generate -DgroupId=com.mycompany.app -DartifactId=my-app -DarchetypeArtifactId=thorntail4-app-archetype -DarchetypeGroupId=io.dekstroza -DarchetypeVersion=1.0.1-SNAPSHOT -DinteractiveMode=false
```
## Building the project
Generated project will have docker build enabled by default, so running
```
# On Linux 
./mvnw clean install
# On Windows
mvnw.cmd clean install
```
will also build docker container as part of the build.

To disable run with:
```
# On Linux
./mvnw clean install -Ddockerfile.skip
# On Windows
mvnw.cmd clean install
```
or permanently set dockerfile.skip property to false in pom.xml of the generated project.
## Running the code

Code can be run from IDE by just running the class with the main method. By default it will be available on localhost:8080
Docker container will be automatically set up to run, as well as k8s deployment descriptors (including metrics and health).
For more informations about Thorntail 4, see: [Thorntail 4 Documentation](https://docs.thorntail.io/4.0.0-SNAPSHOT/)

