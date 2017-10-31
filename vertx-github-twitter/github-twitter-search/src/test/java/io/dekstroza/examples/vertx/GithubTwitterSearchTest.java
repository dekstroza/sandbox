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
import rx.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(VertxUnitRunner.class)
public class GithubTwitterSearchTest {

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
        TwitterApi.createTwitterReactiveApi(vertx).searchTweets(project)
                   .subscribeOn(Schedulers.io())
                   .doAfterTerminate(async::complete)
                   .subscribe(Assert::assertNotNull);
    }

    @Test
    public void testSearchGithubProjects(TestContext context) {
        final Async async = context.async();
        GithubApi.createGithubApi(vertx).searchGitHubProjects("reactive")
                   .subscribeOn(Schedulers.io())
                   .doAfterTerminate(async::complete)
                   .subscribe(Assert::assertNotNull);
    }

    @Test
    public void testGithubTwitterSearch(TestContext context) {
        final Async async = context.async();
        WebClient webClient = WebClient.create(vertx, new WebClientOptions().setDefaultHost("localhost").setSsl(false).setDefaultPort(8080));
        webClient.get(GithubTwitterSearchService.DEFAULT_SERVER_PORT, GithubTwitterSearchService.DEFAULT_LISTEN_ADDRESS, "/search/akka")
                   .rxSend()
                   .subscribe(bufferHttpResponse -> {
                       assertEquals(200,bufferHttpResponse.statusCode());
                       assertNotNull(bufferHttpResponse.bodyAsString());
                       async.complete();
                   });
    }
}
