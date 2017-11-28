package io.dekstroza.github.examples;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import io.dekstroza.github.examples.twitter.TwitterActor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class TwitterActorTest {

    static ActorSystem actorSystem;

    @BeforeClass
    public static void setup() {
        actorSystem = actorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(actorSystem);
        actorSystem = null;
    }

    @Test
    public void testTwitterSearch() {
        final TestKit probe = new TestKit(actorSystem);
        final ActorRef twitterSearchActor = actorSystem.actorOf(Props.create(TwitterActor.class));
        twitterSearchActor.tell("reactive", probe.getRef());
        List<String> response = probe.expectMsgClass(List.class);
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }
}