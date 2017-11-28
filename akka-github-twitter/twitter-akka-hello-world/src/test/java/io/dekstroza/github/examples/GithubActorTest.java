package io.dekstroza.github.examples;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import io.dekstroza.github.examples.github.GitHubProject;
import io.dekstroza.github.examples.github.GithubActor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import scala.concurrent.duration.Duration;

import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class GithubActorTest {

    static ActorSystem actorSystem;
    Duration timeout = Duration.create(5, SECONDS);
    final String searchTerm = "reactive";

    @BeforeClass
    public static void setup() {
        actorSystem = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(actorSystem);
        actorSystem = null;
    }

    @Test
    public void testGitHubSearch() {
        final TestKit probe = new TestKit(actorSystem);
        final ActorRef githubActor = actorSystem.actorOf(Props.create(GithubActor.class));
        githubActor.tell("reactive", probe.getRef());
        List<GitHubProject> result = probe.expectMsgClass(List.class);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

}