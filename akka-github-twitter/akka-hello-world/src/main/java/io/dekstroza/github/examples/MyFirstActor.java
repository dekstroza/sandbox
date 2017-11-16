package io.dekstroza.github.examples;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class MyFirstActor extends AbstractActor {

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    ActorRef target;

    static public Props props(ActorRef target) {
        return Props.create(MyFirstActor.class, () -> new MyFirstActor(target));
    }

    static public Props props() {
        return Props.create(MyFirstActor.class, () -> new MyFirstActor());
    }

    public MyFirstActor() {

    }

    public MyFirstActor(ActorRef target) {
        this.target = target;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(Greeting.class, greeting -> {
            log.info("Received greeting: {}", greeting.getMessage());
            if (target != null) {
                target.forward(greeting, getContext());
            }
        }).build();
    }
}
