package io.dekstroza.examples.vertx;

import io.vertx.core.json.Json;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.client.WebClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.schedulers.Schedulers;

@RunWith(VertxUnitRunner.class)
public class GithubTwitterSearchTest {

    private static final Logger log = LoggerFactory.getLogger(GithubTwitterSearchTest.class);
    private Vertx vertx;

    @Before
    public void setup(TestContext context) {
        vertx = Vertx.vertx();
        vertx.deployVerticle(GithubTwitterSearchService.class.getName(), context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testSearchTweets(TestContext context) throws Exception {
        final Async async = context.async();
        GitHubProject project = new GitHubProject("component/reactive", "Tiny reactive template engine");
        TwitterApi.createTwitterReactiveApi(vertx).searchTweets(project).subscribeOn(Schedulers.io()).doAfterTerminate(() -> {
            async.complete();
        }).subscribe(tweet -> {
            Assert.assertNotNull(tweet);
            log.info("{}", Json.encodePrettily(tweet));
        });
    }

    @Test
    public void testSearchGithubProjects(TestContext context) {
        final Async async = context.async();
        GithubApi.createGithubApi(vertx).searchGitHubProjects("reactive").subscribeOn(Schedulers.io()).doAfterTerminate(() -> {
            async.complete();
        }).subscribe(gitHubProject -> {
            Assert.assertNotNull(gitHubProject);
            log.info(Json.encodePrettily(gitHubProject));
        });
    }

    @Test
    public void testGithubTwitterSearch(TestContext context) {
        final Async async = context.async();
        WebClient webClient = WebClient.create(vertx, new WebClientOptions().setDefaultHost("localhost").setSsl(false).setDefaultPort(8080));
        webClient.get(8080, "localhost", "/search/akka")
                   .rxSend()
                   .subscribe(bufferHttpResponse -> {
                       Assert.assertEquals(200,bufferHttpResponse.statusCode());
                       Assert.assertNotNull(bufferHttpResponse.bodyAsString());
                       async.complete();
                   });
    }
}
