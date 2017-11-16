package io.dekstroza.github.examples;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MyFirstActorTest {
    static ActorSystem actorSystem;
    static final String HELLO_WORLD = "Hello World";

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
        final ActorRef myFirstActor = actorSystem.actorOf(MyFirstActor.props(probe.getRef()));
        myFirstActor.tell(new Greeting(HELLO_WORLD), probe.getRef());
        final Greeting greeting = probe.expectMsgClass(Greeting.class);
        assertEquals(HELLO_WORLD, greeting.getMessage());

    }

}
