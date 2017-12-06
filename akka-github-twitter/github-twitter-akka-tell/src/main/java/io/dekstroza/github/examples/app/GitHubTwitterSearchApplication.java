package io.dekstroza.github.examples.app;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.marshalling.Marshaller;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import io.dekstroza.github.examples.common.SearchSummary;
import io.dekstroza.github.examples.common.actors.TwitterTokenActor;

import java.util.List;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.Directives.completeWith;
import static akka.http.javadsl.server.Directives.path;
import static akka.http.javadsl.server.PathMatchers.segment;
import static java.util.regex.Pattern.compile;

public class GitHubTwitterSearchApplication {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8080;
    private final ActorRef searchActor;
    private final ActorRef twitterTokenActor;
    private ActorSystem system;

    private GitHubTwitterSearchApplication(ActorSystem system) {
        this.system = system;
        searchActor = system.actorOf(Props.create(SearchActor.class), "requestProcessor");
        twitterTokenActor = system.actorOf(TwitterTokenActor.props(), TwitterTokenActor.NAME);

    }

    public static void main(String[] args) throws Exception {
        ActorSystem system = ActorSystem.create("github-twitter-akka-nje");
        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        final GitHubTwitterSearchApplication app = new GitHubTwitterSearchApplication(system);
        final LoggingAdapter log = Logging.getLogger(system, app);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.createRoute().flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow, ConnectHttp.toHost(DEFAULT_HOST, DEFAULT_PORT), materializer);

        log.info("Server online at http://{}:{}", DEFAULT_HOST, DEFAULT_PORT);
        log.info("To search do http get on http://{}:{}/search/YOUR_SERCH_TERM", DEFAULT_HOST, DEFAULT_PORT);
        log.info("Press RETURN to stop the server...");
        System.in.read();
        binding.thenCompose(ServerBinding::unbind).thenAccept(unbound -> system.terminate());
    }

    private Route createRoute() {
        final Marshaller<List<SearchSummary>, HttpResponse> marshaller = Marshaller.entityToOKResponse(Jackson.<List<SearchSummary>>marshaller());
        return path(segment("search").slash(segment(compile("\\w+"))),
                   searchWord -> completeWith(marshaller, completionFunction -> searchActor.tell(new Request(completionFunction, searchWord), null)));
    }
}


