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
