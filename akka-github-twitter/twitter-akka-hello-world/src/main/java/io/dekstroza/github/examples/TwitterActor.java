package io.dekstroza.github.examples;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.Http;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.FormData;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.headers.RawHeader;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import io.dekstroza.github.examples.io.dekstroza.github.examples.config.Settings;
import io.dekstroza.github.examples.io.dekstroza.github.examples.config.SettingsImpl;
import scala.concurrent.ExecutionContextExecutor;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletionStage;

import static akka.pattern.PatternsCS.pipe;
import static java.lang.String.format;
import static java.net.URLEncoder.encode;
import static java.util.Base64.getEncoder;

public class TwitterActor extends AbstractActor {

    private static final String ENCODING = "UTF-8";
    private final SettingsImpl settings = Settings.SettingsProvider.get(getContext().getSystem());
    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final Http http = Http.get(getContext().getSystem());
    private final ExecutionContextExecutor dispatcher = context().dispatcher();
    private final Materializer materializer = ActorMaterializer.create(context());

    public static Props props() {
        return Props.create(TwitterActor.class, () -> new TwitterActor());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(String.class, url -> {
            pipe(fetch(url), dispatcher).to(sender());
        }).build();
    }

    CompletionStage<TokenResponse> fetch(String url) {
        return http.singleRequest(HttpRequest.POST(url).addHeader(RawHeader
                   .create("Authorization", "Basic " + encodeConsumerKeyAndSecret(settings.CONSUMER_KEY, settings.CONSUMER_SECRET)))
                   .withEntity(FormData.create(new Pair<>("grant_type", "client_credentials")).toEntity()), materializer).thenCompose(response -> {
            if (response.status().isSuccess()) {
                return Jackson.unmarshaller(TokenResponse.class).unmarshal(response.entity(), materializer);
            } else
                throw new RuntimeException(format("Unable to obtain token, response status is:%s, and response message is:%s." + response.status(),
                           response.status().reason()));
        });
    }

    private String encodeConsumerKeyAndSecret(final String consumerKey, final String consumerSecret) {
        final String encodedConsumerKey = urlEncodeString(consumerKey);
        final String encodedConsumerSecret = urlEncodeString(consumerSecret);
        return getEncoder().encodeToString(format("%s:%s", encodedConsumerKey, encodedConsumerSecret).getBytes());
    }

    private String urlEncodeString(final String str) {
        try {
            return encode(str, ENCODING);
        } catch (UnsupportedEncodingException uee) {
            log.error("Error url encoding string to {}:", ENCODING, uee.getMessage());
            return str;
        }
    }
}
