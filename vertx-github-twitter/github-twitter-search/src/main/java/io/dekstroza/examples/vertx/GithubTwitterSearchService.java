package io.dekstroza.examples.vertx;

import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.schedulers.Schedulers;

public class GithubTwitterSearchService extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(GithubTwitterSearchService.class);

    @Override
    public void start(Future<Void> fut) {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route(HttpMethod.GET, "/search/:keyword").handler(routingContext -> {
            HttpServerRequest request = routingContext.request();
            HttpServerResponse response = routingContext.response();
            response.setChunked(true);
            response.putHeader("Content-Type", "application/json");

            GithubApi.createGithubApi(vertx).searchGitHubProjects(request.getParam("keyword"))
                       .observeOn(Schedulers.io())
                       .take(10)
                       .flatMap(gitHubProject -> {
                           return TwitterApi.createTwitterReactiveApi(vertx).searchTweets(gitHubProject)
                                      .take(10)
                                      .toList()
                                      .map(tweets -> new Summary(gitHubProject, tweets));
                       })
                       .toList()
                       .subscribe(summaries -> {
                           response.end(Json.encodePrettily(summaries));
                       });
        });
        server.requestStream()
                   .toObservable()
                   .subscribe(httpServerRequest -> router.accept(httpServerRequest));

        server.listen(8080, "0.0.0.0", httpServerAsyncResult -> {
            if (httpServerAsyncResult.succeeded()) {
                fut.complete();
            } else {
                fut.fail(httpServerAsyncResult.cause());
            }
        });
    }
}
