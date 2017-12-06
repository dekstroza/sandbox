package io.dekstroza.github.examples;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.PatternsCS;
import akka.testkit.javadsl.TestKit;
import akka.util.Timeout;
import io.dekstroza.github.examples.common.TokenRequestMessage;
import io.dekstroza.github.examples.common.TokenResponse;
import io.dekstroza.github.examples.common.actors.TwitterTokenActor;
import io.dekstroza.github.examples.twitter.TwitterActor;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class TwitterActorTest {

    static ActorSystem actorSystem;

    @BeforeClass
    public static void setup() throws Exception {
        actorSystem = actorSystem.create("github-twitter-akka-nje");
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(actorSystem);
        actorSystem = null;
    }

    @Test
    public void testTwitterSearch() {
        final TestKit probe = new TestKit(actorSystem);
        final ActorRef twitterTokenActor = actorSystem.actorOf(TwitterTokenActor.props(), TwitterTokenActor.NAME);
        final CompletableFuture<TokenResponse> tokenResponseCompletionStage = PatternsCS.ask(twitterTokenActor, new TokenRequestMessage(),
                   Timeout.apply(5, SECONDS)).thenApply(TokenResponse.class::cast).toCompletableFuture();
        final TokenResponse join = tokenResponseCompletionStage.join();
        Assert.assertNotNull(join);
        final ActorRef twitterSearchActor = actorSystem.actorOf(Props.create(TwitterActor.class));
        twitterSearchActor.tell("reactive", probe.getRef());
        List<String> response = probe.expectMsgClass(List.class);
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }
}