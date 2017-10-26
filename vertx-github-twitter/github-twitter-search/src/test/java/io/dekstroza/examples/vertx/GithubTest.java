package io.dekstroza.examples.vertx;

import io.vertx.core.json.Json;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

@RunWith(VertxUnitRunner.class)
public class GithubTest {

    private Vertx vertx;
    private static final Logger log = LoggerFactory.getLogger(GithubTest.class);

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
    public void testSearchGithubProjects(TestContext context) {
        final Async async = context.async();
        final Observable<GitHubProject> reactive = GithubApi.createGithubApi(vertx).searchGitHubProjects("reactive");

        reactive.subscribeOn(Schedulers.io()).doAfterTerminate(() -> {
            async.complete();
        }).subscribe(gitHubProject -> {
            Assert.assertNotNull(gitHubProject);
            log.info(Json.encodePrettily(gitHubProject));
        });
    }
}
