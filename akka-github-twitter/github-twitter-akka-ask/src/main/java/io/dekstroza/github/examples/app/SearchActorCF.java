package io.dekstroza.github.examples.app;

import akka.pattern.PatternsCS;

public class SearchActorCF extends SearchActor {

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(String.class, searchTerm -> PatternsCS.pipe(performSearchCF(searchTerm), dispatcher).to(getSender())).build();
    }
}
