package io.dekstroza.github.examples.app;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import akka.pattern.PatternsCS;
import akka.routing.RoundRobinPool;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import akka.util.Timeout;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;
import scala.reflect.ClassTag;

import java.util.concurrent.CompletionStage;

import static akka.actor.Props.create;
import static akka.http.javadsl.server.PathMatchers.segment;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.regex.Pattern.compile;

public class TwitterAkkaApplication extends AllDirectives {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8080;

    private ActorRef searchActor;

    private TwitterAkkaApplication(final ActorSystem system) {

        searchActor = system.actorOf(create(SearchActor.class).withRouter(new RoundRobinPool(10)), "search-actors");
    }

    public static void main(String[] args) throws Exception {
        ActorSystem system = ActorSystem.create("github-twitter-akka-nje");
        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        final TwitterAkkaApplication app = new TwitterAkkaApplication(system);
        final LoggingAdapter log = Logging.getLogger(system, app);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.createRoute().flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow, ConnectHttp.toHost(DEFAULT_HOST, DEFAULT_PORT), materializer);

        log.info("Server online at http://{}:{}", DEFAULT_HOST, DEFAULT_PORT);
        log.info("To search with scala Futures do http GET on http://{}:{}/search/YOUR_SERCH_TERM", DEFAULT_HOST, DEFAULT_PORT);
        log.info("To search with java CompletionStages do http GET on http://{}:{}/searchCS/YOUR_SERCH_TERM", DEFAULT_HOST, DEFAULT_PORT);
        log.info("Press RETURN to stop the server...");
        System.in.read();
        binding.thenCompose(ServerBinding::unbind).thenAccept(unbound -> system.terminate());
    }

    private Route createRoute() {
        return path(segment("search").slash(segment(compile("\\w+"))),
                   searchWord -> completeOKWithFuture(searchAndSummarize(searchWord), Jackson.marshaller())).orElse(createRouteCS());
    }

    private Route createRouteCS() {
        return path(segment("searchCS").slash(segment(compile("\\w+"))),
                   searchWord -> completeOKWithFuture(searchAndSummarizeCS(searchWord), Jackson.marshaller()));
    }

    private Future<Iterable<SearchSummary>> searchAndSummarize(final String keyword) {
        Timeout timeout = Timeout.durationToTimeout(FiniteDuration.apply(15, SECONDS));
        return Patterns.ask(searchActor, keyword, timeout).mapTo(ClassTag.apply(Iterable.class));
    }

    private CompletionStage<Iterable<SearchSummary>> searchAndSummarizeCS(final String keyword) {
        Timeout timeout = Timeout.durationToTimeout(FiniteDuration.apply(15, SECONDS));
        return PatternsCS.ask(searchActor, keyword, timeout).thenApply(Iterable.class::cast);
    }

}


