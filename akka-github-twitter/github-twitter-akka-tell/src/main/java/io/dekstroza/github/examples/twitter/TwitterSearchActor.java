package io.dekstroza.github.examples.twitter;

import akka.actor.AbstractActor;
import akka.actor.Cancellable;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.headers.RawHeader;
import akka.http.javadsl.unmarshalling.Unmarshaller;
import akka.pattern.PatternsCS;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.util.Timeout;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.dekstroza.github.examples.common.*;
import io.dekstroza.github.examples.common.actors.TwitterTokenActor;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.duration.FiniteDuration;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.model.HttpRequest.GET;
import static io.dekstroza.github.examples.common.Constants.TWEETS_QUERY;
import static io.dekstroza.github.examples.common.Constants.jsonPathConfig;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static scala.concurrent.duration.Duration.create;

public class TwitterSearchActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private Cancellable timeout;
    private final GitHubProject githubProject;

    private final ExecutionContextExecutor dispatcher = context().dispatcher();
    private final Materializer materializer = ActorMaterializer.create(context());
    private final Http http = Http.get(getContext().getSystem());

    private TwitterSearchActor(GitHubProject searchTerm) {
        this.githubProject = searchTerm;
    }

    public static Props props(final GitHubProject githubProject) {
        return Props.create(TwitterSearchActor.class, () -> new TwitterSearchActor(githubProject));
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        searchTweets();
        scheduleTimeout();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(TimeoutMessage.class, timeoutMessage -> {
            timeout.cancel();
            log.error("Timeout happened, notifying parent.");
            getContext().getParent().tell(timeoutMessage, getSelf());
            getContext().stop(self());
        }).build();

    }

    private void scheduleTimeout() {
        timeout = context().system().scheduler().scheduleOnce(create(5, SECONDS), getSelf(), new TimeoutMessage(), dispatcher, getSelf());
    }

    private void searchTweets() {
        getContext().getSystem().actorSelection(Constants.TWITTER_TOKEN_ACTOR_PATH).resolveOneCS(FiniteDuration.apply(5, SECONDS)).thenApply(
                   actorRef -> PatternsCS.ask(actorRef, new TokenRequestMessage(), Timeout.apply(5, SECONDS)).thenApply(TokenResponse.class::cast)
                              .thenApply(tokenResponse -> searchTwitter(githubProject.getName(), tokenResponse.getAccessToken())
                                         .thenAccept(this::sendResponse)));
    }

    private void sendResponse(List<Tweet> tweets) {
        timeout.cancel();
        getContext().getParent().tell(new TwitterSearchResponse(tweets, githubProject, Optional.empty()), self());
        getContext().stop(self());
    }

    private CompletionStage<List<Tweet>> searchTwitter(String keyword, String twitterToken) {
        String searchUrl = format(Constants.SEARCH_URL, keyword);
        RawHeader rawHeader = RawHeader.create("Authorization", format("Bearer %s", twitterToken));
        return http.singleRequest(GET(searchUrl).addHeader(rawHeader), materializer).thenCompose(this::unmarshalToString).thenApply(
                   this::parseTweets);
    }

    private CompletionStage<String> unmarshalToString(HttpResponse response) {
        return Unmarshaller.entityToString().unmarshal(response.entity(), materializer);
    }

    private List<Tweet> parseTweets(String json) {
        final DocumentContext ctx = JsonPath.using(jsonPathConfig).parse(json);
        return ((List<String>) ctx.read(TWEETS_QUERY)).stream().limit(10).map(Tweet::new).collect(toList());
    }

}
