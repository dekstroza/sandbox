package io.dekstroza.github.examples.app;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.dekstroza.github.examples.github.GitHubSearchActor;
import io.dekstroza.github.examples.github.TerminateMeMessage;

public class SearchActor extends AbstractActor {

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    public static Props props = Props.create(SearchActor.class, () -> new SearchActor());

    private SearchActor() {
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(Request.class, this::handleRequest).match(TerminateMeMessage.class, terminateMeMessage -> removeChild(sender()))
                   .build();
    }

    private void removeChild(ActorRef child) {
        getContext().stop(child);
    }

    private void handleRequest(Request request) {
        getContext().actorOf(GitHubSearchActor.props(request));
    }

}
