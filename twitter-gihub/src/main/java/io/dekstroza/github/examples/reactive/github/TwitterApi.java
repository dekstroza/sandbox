package io.dekstroza.github.examples.reactive.github;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.reactivex.Observable;
import okhttp3.*;
import okhttp3.Request.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import static com.jayway.jsonpath.JsonPath.parse;
import static io.reactivex.Observable.create;
import static java.lang.String.format;
import static java.net.URLEncoder.encode;
import static java.util.Base64.getEncoder;
import static okhttp3.RequestBody.create;

public class TwitterApi {

    private static final Logger log = LoggerFactory.getLogger(TwitterApi.class);
    private static final String ENCODING = "UTF-8";
    private static final MediaType TWITTER_MEDIA_TYPE = MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8");
    private static final String TWITER_OAUTH_URL = "https://api.twitter.com/oauth2/token";
    private static final String TWITTER_SEARCH_URL = "https://api.twitter.com/1.1/search/tweets.json?q=%s";
    private static TwitterApi instance;

    private String consumerKey;
    private String consumerSecret;
    private String bearerToken;

    private TwitterApi() {
        final Properties properties = new Properties();
        try (final InputStream stream = this.getClass().getResourceAsStream("/twitter.properties")) {
            properties.load(stream);
            this.consumerKey = properties.getProperty("consumerKey");
            this.consumerSecret = properties.getProperty("consumerSecret");
            this.bearerToken = getTwitterBearerToken();
        } catch (IOException ioe) {
            throw new IllegalStateException("Unable to load keys or obtain token.", ioe);
        }

    }

    private String encodeConsumerKeyAndSecret(final String consumerKey, final String consumerSecret) throws UnsupportedEncodingException {
        String encodedConsumerKey = encode(consumerKey, ENCODING);
        String encodedConsumerSecret = encode(consumerSecret, ENCODING);
        return getEncoder().encodeToString(format("%s:%s", encodedConsumerKey, encodedConsumerSecret).getBytes());
    }

    private String obtainBearerToken(final String encodedConsumerKeyAndSecret) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = create(TWITTER_MEDIA_TYPE, "grant_type=client_credentials");
        Request request = new Builder().url(TWITER_OAUTH_URL).post(body)

                   .addHeader("Authorization", format("Basic %s", encodedConsumerKeyAndSecret)).build();
        Response response = client.newCall(request).execute();
        return parse(response.body().string()).read("$.access_token").toString();
    }

    private String getTwitterBearerToken() {
        try {
            return bearerToken == null ? (bearerToken = obtainBearerToken(encodeConsumerKeyAndSecret(consumerKey, consumerSecret))) : bearerToken;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to encode twitter credentials to UTF-8");
        }
    }

    public static TwitterApi createTwitterReactiveApi() {
        return instance == null ? (instance = new TwitterApi()) : instance;
    }

    public Observable<Tweet> searchTweets(final GitHubProject project) throws IOException {

        OkHttpClient client = new OkHttpClient();
        Request request = new Builder().url(format(TWITTER_SEARCH_URL, encode(project.getProjectName(), "UTF-8"))).addHeader("Authorization",
                   format("Bearer %s", getTwitterBearerToken())).build();

        DocumentContext ctx = JsonPath.parse(client.newCall(request).execute().body().byteStream());

        return create(emitter2 -> {
            try {
                ctx.read("$.statuses[*].text", List.class).stream().forEach(x -> emitter2.onNext(new Tweet((String) x)));
                emitter2.onComplete();
            } catch (Exception e) {
                emitter2.onError(e);
            }
        });
    }
}
