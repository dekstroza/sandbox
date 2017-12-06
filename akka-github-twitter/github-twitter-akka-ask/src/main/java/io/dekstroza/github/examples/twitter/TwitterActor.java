package io.dekstroza.github.examples.twitter;

import akka.actor.AbstractActor;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.headers.RawHeader;
import akka.http.javadsl.unmarshalling.Unmarshaller;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.dekstroza.github.examples.common.Constants;
import io.dekstroza.github.examples.common.TokenRequestMessage;
import io.dekstroza.github.examples.common.TokenResponse;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import java.util.List;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.model.HttpRequest.GET;
import static akka.pattern.PatternsCS.ask;
import static akka.util.Timeout.apply;
import static com.jayway.jsonpath.Option.ALWAYS_RETURN_LIST;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static scala.compat.java8.FutureConverters.toScala;

public class TwitterActor extends AbstractActor {

    private static final Configuration jsonPathConfig = Configuration.builder().options(ALWAYS_RETURN_LIST).build();
    private static final String SEARCH_URL = "https://api.twitter.com/1.1/search/tweets.json?q=%s";
    private static final String TWEETS_QUERY = "$.statuses[*].text";
    private final ExecutionContextExecutor dispatcher = context().dispatcher();
    private final Materializer materializer = ActorMaterializer.create(context());
    private final Http http = Http.get(getContext().getSystem());

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(String.class, keyword -> Patterns.pipe(searchTweets(keyword), dispatcher).to(sender())).build();
    }

    private Future<List<Tweet>> searchTweets(String keyword) {
        return toScala(getContext().getSystem().actorSelection(Constants.TWITTER_TOKEN_ACTOR_PATH).resolveOneCS(FiniteDuration.apply(5, SECONDS))
                   .thenCompose(actorRef -> ask(actorRef, new TokenRequestMessage(), apply(5, SECONDS)).thenApply(TokenResponse.class::cast)
                              .thenCompose(tokenResponse -> searchTwitter(keyword, tokenResponse.getAccessToken()))));

    }

    private CompletionStage<List<Tweet>> searchTwitter(String keyword, String authToken) {
        String searchUrl = format(SEARCH_URL, keyword);
        RawHeader rawHeader = RawHeader.create("Authorization", format("Bearer %s", authToken));
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
