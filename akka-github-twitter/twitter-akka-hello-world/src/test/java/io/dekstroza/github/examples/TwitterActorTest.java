package io.dekstroza.github.examples;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.testkit.javadsl.TestKit;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

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
    public void testMyFirstActor_Greeting() {
        final TestKit probe = new TestKit(actorSystem);
        final ActorRef twitterActor = actorSystem.actorOf(TwitterActor.props());
        twitterActor.tell("https://api.twitter.com/oauth2/token", probe.getRef());
        TokenResponse response = probe.expectMsgClass(TokenResponse.class);
        Assert.assertFalse(response.getAccessToken().isEmpty());
        System.out.println(response.getAccessToken());
    }
}