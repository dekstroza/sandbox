# Custom thorntatil4 branch project archetype

## Instructions

Clone project and install archetype with:
```
mvn clean install
```
## Generate projects

Use the archetype to generate projects. All properties have default values.
```
# sets docker repo by default to this value, can be overriden when generating project
dockerRepo=dekstroza 
```
To generate project use usual maven archetype commands, for example:
```
mvn archetype:generate -DgroupId=com.mycompany.app -DartifactId=my-app -DarchetypeArtifactId=thorntail4-app-archetype -DarchetypeGroupId=io.dekstroza -DarchetypeVersion=1.0.1-SNAPSHOT -DinteractiveMode=false
```

Generated project will have docker build enabled by default. To disable run with:
```
./mvnw clean install -Ddockerfile.skip
```
or permanently set it to false in pom.xml of the generated project.
