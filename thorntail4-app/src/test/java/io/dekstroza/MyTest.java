package io.dekstroza;

import io.thorntail.test.ThorntailTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsString;

@RunWith(ThorntailTestRunner.class)
public class MyTest {

    private static final Logger log = LoggerFactory.getLogger(MyTest.class);
    private static final String SENT_MESSAGE = "Hello Testing World";
    private static final String SERVER_URL = "ws://localhost:8080/ws";
    private CountDownLatch latch = new CountDownLatch(1);
    private String result;

    @Test
    public void test() {
        when().get("/").then().statusCode(200).body(containsString("Hello World"));
    }

}

