package io.dekstroza.github.examples;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class MyFirstActor extends AbstractActor {

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    static public Props props() {
        return Props.create(MyFirstActor.class, () -> new MyFirstActor());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(Greeting.class, greeting -> {
            log.info("Received greeting: {}", greeting.getMessage());
            sender().forward(greeting, getContext());
        }).build();
    }
}
