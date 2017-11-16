package io.dekstroza.github.examples;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class HelloWorldApplication {

    public static void main(String[] args) {
        final ActorSystem actorSystem = ActorSystem.create("HelloWorldApplication");
        try {
            final ActorRef myFirstActor = actorSystem.actorOf(MyFirstActor.props(), "MyFirstActor");
            myFirstActor.tell(new Greeting("Hello World"), ActorRef.noSender());
        } finally {
            actorSystem.terminate();
        }
    }

}
