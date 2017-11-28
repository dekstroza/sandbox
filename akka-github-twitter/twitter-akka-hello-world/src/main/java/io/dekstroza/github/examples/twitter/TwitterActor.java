package io.dekstroza.github.examples.twitter;

import akka.actor.AbstractActor;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.FormData;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.RequestEntity;
import akka.http.javadsl.model.headers.RawHeader;
import akka.http.javadsl.unmarshalling.Unmarshaller;
import akka.japi.Pair;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.dekstroza.github.examples.config.Settings;
import io.dekstroza.github.examples.config.SettingsImpl;
import scala.compat.java8.FutureConverters;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.Future;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.marshallers.jackson.Jackson.unmarshaller;
import static akka.http.javadsl.model.HttpRequest.GET;
import static akka.http.javadsl.model.HttpRequest.POST;
import static akka.http.javadsl.model.headers.RawHeader.create;
import static com.jayway.jsonpath.Option.ALWAYS_RETURN_LIST;
import static java.lang.String.format;
import static java.util.Base64.getEncoder;
import static java.util.stream.Collectors.toList;

public class TwitterActor extends AbstractActor {

    private static final Configuration jsonPathConfig = Configuration.builder().options(ALWAYS_RETURN_LIST).build();
    private static final Pair<String, String> TOKEN_REQ_BODY = new Pair<>("grant_type", "client_credentials");
    private static final RequestEntity TOKEN_REQ_ENTITY = FormData.create(TOKEN_REQ_BODY).toEntity();
    private static final String AUTH_URL = "https://api.twitter.com/oauth2/token";
    private static final String SEARCH_URL = "https://api.twitter.com/1.1/search/tweets.json?q=%s";
    private static final String ENCODING = "UTF-8";
    private static final String TWEETS_QUERY = "$.statuses[*].text";

    private final SettingsImpl configuration = Settings.SettingsProvider.get(getContext().getSystem());
    private final RawHeader authHeader = create("Authorization",
               format("Basic %s", base64Encode(configuration.CONSUMER_KEY, configuration.CONSUMER_SECRET)));
    private final ExecutionContextExecutor dispatcher = context().dispatcher();
    private final Materializer materializer = ActorMaterializer.create(context());
    private final Http http = Http.get(getContext().getSystem());

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(String.class, keyword -> Patterns.pipe(searchTweets(keyword), dispatcher).to(sender())).build();
    }

    private Future<List<Tweet>> searchTweets(String keyword) {
        return FutureConverters.toScala(getTwitterAuthToken().thenCompose(tokenResponse -> searchTwitter(keyword, tokenResponse.getAccessToken())));

    }

    private CompletionStage<List<Tweet>> searchTweetsCF(String keyword) {
        return getTwitterAuthToken().thenCompose(tokenResponse -> searchTwitter(keyword, tokenResponse.getAccessToken()));

    }

    private CompletionStage<TokenResponse> getTwitterAuthToken() {
        return http.singleRequest(POST(AUTH_URL).addHeader(authHeader).withEntity(TOKEN_REQ_ENTITY), materializer).thenCompose(
                   this::unmarshalToTokenResponse);
    }

    private CompletionStage<List<Tweet>> searchTwitter(String keyword, String authToken) {
        String searchUrl = format(SEARCH_URL, keyword);
        RawHeader rawHeader = RawHeader.create("Authorization", format("Bearer %s", authToken));
        return http.singleRequest(GET(searchUrl).addHeader(rawHeader), materializer).thenCompose(this::unmarshalToString).thenApply(
                   this::parseTweets);
    }

    private CompletionStage<TokenResponse> unmarshalToTokenResponse(HttpResponse response) {
        return unmarshaller(TokenResponse.class).unmarshal(response.entity(), materializer);
    }

    private CompletionStage<String> unmarshalToString(HttpResponse response) {
        return Unmarshaller.entityToString().unmarshal(response.entity(), materializer);
    }

    private List<Tweet> parseTweets(String json) {
        final DocumentContext ctx = JsonPath.using(jsonPathConfig).parse(json);
        return ((List<String>) ctx.read(TWEETS_QUERY)).stream().limit(10).map(Tweet::new).collect(toList());
    }

    private String base64Encode(final String consumerKey, final String consumerSecret) {
        final String encodedConsumerKey = urlEncodeString(consumerKey);
        final String encodedConsumerSecret = urlEncodeString(consumerSecret);
        return getEncoder().encodeToString(format("%s:%s", encodedConsumerKey, encodedConsumerSecret).getBytes());
    }

    private String urlEncodeString(final String str) {
        try {
            return URLEncoder.encode(str, ENCODING);
        } catch (UnsupportedEncodingException uee) {
            return str;
        }
    }
}
