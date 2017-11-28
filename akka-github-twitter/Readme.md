# Akka implementation of github-twitter search

First attempt to implement this in akka. Contains two maven modules:

1. akka-hello-world
2. twitter-akka-hello-world

First module is simplest possible Hello World single actor system, it receives string message and outputs it to stdout.
Second module is implementation of project search on github, crossreferenced with twitter search for tweets mentioning it. 
It has implementation with both scala Future and Java's CompletionStage for reference.

## Requirements

To build and run it needs:
1. Java 8
2. Maven 3 

## Building

Before building twitter-akka-hello-world, create file twitter.creds next to it's pom.xml. File should contain your twitter consumer key and secret, like shown here:
```
consumer.key=KEY_HERE
consumer.secret=SECRET_HERE
```
Consumer key and secret can be obtained from twitter, for more info go to: https://apps.twitter.com

Once this is done, from main pom file directory, run:

```
mvn clean install
```

## Running

Once compiled, it can be run from IDE as standalone java application or via command line. To run from command line do:

```
java -jar akka-hello-world/target/akka-hello-world-1.0.1-SNAPSHOT.jar
```

or to run search github-twitter example:

```
java -jar twitter-akka-hello-world/target/twitter-akka-hello-world-1.0.1-SNAPSHOT-allinone.jar
```
Once above is running (for search example), you can access it on:
```
http://localhost:8080/search[CS]/SEARCH_TERM 
```
and hit any key to stop the web server. CS suffix will use search implemented with Java CompletionStage.
