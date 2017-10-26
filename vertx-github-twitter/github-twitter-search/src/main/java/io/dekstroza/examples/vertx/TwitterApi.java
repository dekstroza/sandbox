package io.dekstroza.examples.vertx;

import com.jayway.jsonpath.JsonPath;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.ext.web.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Single;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.net.URLEncoder.encode;
import static java.util.Base64.getEncoder;

public class TwitterApi {

    private static final Logger log = LoggerFactory.getLogger(TwitterApi.class);
    private static final String ENCODING = "UTF-8";
    private static final String TWITTER_MEDIA_TYPE = "application/x-www-form-urlencoded;charset=UTF-8";
    private static final String TWITER_OAUTH_URL = "https://api.twitter.com/oauth2/token";
    private static final String TWITTER_SEARCH_URL = "https://api.twitter.com/1.1/search/tweets.json?q=%s";
    private static TwitterApi inst;

    private static String consumerKey;
    private static String consumerSecret;
    private static String bearerToken;
    private static String encodedConsumerSecretKey;
    private Vertx vertx;

    private TwitterApi(Vertx vertx) {
        final Properties properties = new Properties();
        try (final InputStream stream = this.getClass().getResourceAsStream("/twitter.properties")) {
            properties.load(stream);
            this.consumerKey = properties.getProperty("consumerKey");
            this.consumerSecret = properties.getProperty("consumerSecret");
            this.encodedConsumerSecretKey = encodeConsumerKeyAndSecret(this.consumerKey, this.consumerSecret);
            this.vertx = vertx;
        } catch (IOException ioe) {
            throw new IllegalStateException("Unable to load keys or obtain token.", ioe);
        }

    }

    private String encodeConsumerKeyAndSecret(final String consumerKey, final String consumerSecret) {
        final String encodedConsumerKey = urlEncodeString(consumerKey);
        final String encodedConsumerSecret = urlEncodeString(consumerSecret);
        return getEncoder().encodeToString(format("%s:%s", encodedConsumerKey, encodedConsumerSecret).getBytes());
    }

    private String urlEncodeString(final String str) {
        try {
            return encode(str, "UTF-8");
        } catch (UnsupportedEncodingException uee) {
            log.error("Error url encoding string to UTF-8:", uee.getMessage());
            return str;
        }
    }

    Single<Tuple<String, String>> obtainToken() {
        return WebClient.create(vertx, new WebClientOptions().setSsl(true).setFollowRedirects(true).setDefaultPort(443)).post(443,
                   "api.twitter.com", "/oauth2/token").putHeader("Authorization", "Basic " + encodedConsumerSecretKey).putHeader("Content-Type",
                   TWITTER_MEDIA_TYPE).rxSendBuffer(Buffer.buffer("grant_type=client_credentials")).map(bufferHttpResponse -> JsonPath.parse(
                   bufferHttpResponse.bodyAsString()).read("$.access_token").toString()).map(s -> "Bearer " + s).map(
                   s -> new Tuple<>("Authorization", s)).cache();
    }

    public static TwitterApi createTwitterReactiveApi(Vertx vertx) {
        return inst == null ? (inst = new TwitterApi(vertx)) : inst;
    }

    public Observable<Tweet> searchTweets(final GitHubProject project) {
        return obtainToken().flatMapObservable(stringStringTuple -> {
            return WebClient.create(vertx, new WebClientOptions().setSsl(true).setDefaultPort(443)).get(443, "api.twitter.com",
                       format("/1.1/search/tweets.json?q=%s", urlEncodeString(project.getProjectName()))).putHeader(stringStringTuple.first,
                       stringStringTuple.second).rxSend().map(bufferHttpResponse -> bufferHttpResponse.bodyAsString()).map(
                       body -> JsonPath.parse(body)).map(ctx -> ctx.read("$.statuses[*].text", List.class)).toObservable().flatMap(list -> {
                return Observable.from(((List<String>) list).stream().map(s -> new Tweet(s)).collect(Collectors.toList()));
            });
        });
    }


}
