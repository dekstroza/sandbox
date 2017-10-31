package io.dekstroza.examples.vertx;

import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.web.Router;
import rx.schedulers.Schedulers;

public class GithubTwitterSearchService extends AbstractVerticle {

    static final int DEFAULT_SERVER_PORT = 8080;
    static final String DEFAULT_LISTEN_ADDRESS = "0.0.0.0";

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
                       .flatMap(gitHubProject -> TwitterApi.createTwitterReactiveApi(vertx).searchTweets(gitHubProject)
                                  .take(10)
                                  .toList()
                                  .map(tweets -> new Summary(gitHubProject, tweets)))
                       .toList()
                       .subscribe(summaries -> response.end(Json.encodePrettily(summaries)));
        });
        server.requestStream()
                   .toObservable()
                   .subscribe(router::accept);

        server.listen(DEFAULT_SERVER_PORT, DEFAULT_LISTEN_ADDRESS, httpServerAsyncResult -> {
            if (httpServerAsyncResult.succeeded()) {
                fut.complete();
            } else {
                fut.fail(httpServerAsyncResult.cause());
            }
        });
    }
}
