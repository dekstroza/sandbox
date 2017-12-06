# Akka implementation of github-twitter search

First attempt to implement this in akka. Contains two maven modules:

1. akka-hello-world
2. github-twitter-akka-ask
3. github-twitter-akka-tell
4. github-twitter-akka-common

First module is simplest possible Hello World single actor system, it receives string message and outputs it to stdout.
Second and third modules are implementations of project search on github, crossreferenced with twitter search for tweets mentioning it.
Third module contains some common bits and bobs used by other. 

It has implementation with both scala Future and Java's CompletionStage for reference. Third example is implementation based on tell (almost completely) with dedicated actor used to fetch and store credentials for twitter, avoiding repeated credential requesting via twitter api.

## Requirements

To build and run it needs:
1. Java 8
2. Maven 3 

## Building

Before building, create file twitter.creds in the same directory as main project pom.xml file. File should contain your twitter consumer key and secret, like shown here:
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

or to run search examples, github-twitter-akka-ask example:

```
java -jar github-twitter-akka-ask/target/github-twitter-akka-ask-1.0.1-SNAPSHOT-allinone.jar
```
or github-twitter-akka-tell example:
```
java -jar github-twitter-akka-tell/target/github-twitter-akka-tell-1.0.1-SNAPSHOT-allinone.jar
```
Once above is running (for search example), you can access it on:
```
http://localhost:8080/search[CS]/SEARCH_TERM 
```
and hit any key to stop the web server. CS suffix will use search implemented with Java CompletionStage.
