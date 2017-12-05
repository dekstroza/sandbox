package io.dekstroza.github.examples.twitter;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.headers.RawHeader;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import io.dekstroza.github.examples.common.TokenResponse;
import io.dekstroza.github.examples.common.config.Settings;
import io.dekstroza.github.examples.common.config.SettingsImpl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.marshallers.jackson.Jackson.unmarshaller;
import static akka.http.javadsl.model.HttpRequest.POST;
import static akka.http.javadsl.model.headers.RawHeader.create;
import static io.dekstroza.github.examples.twitter.Constants.*;
import static java.lang.String.format;
import static java.util.Base64.getEncoder;

public class TwitterTokenActor extends AbstractActor {

    private final SettingsImpl configuration = Settings.SettingsProvider.get(getContext().getSystem());

    private final RawHeader authHeader = create("Authorization",
               format("Basic %s", base64Encode(configuration.CONSUMER_KEY, configuration.CONSUMER_SECRET)));

    private final Materializer materializer = ActorMaterializer.create(context());
    private final Http http = Http.get(getContext().getSystem());

    private TokenResponse token;

    public static Props props() {
        return Props.create(TwitterTokenActor.class, TwitterTokenActor::new);
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        obtainTwitterAuthToken();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(TokenRequestMessage.class, tokenRequestMessage -> sendToken(getSender())).build();
    }

    private void sendToken(ActorRef sender) {
        sender.tell(this.token, null);
    }

    private void obtainTwitterAuthToken() {
        http.singleRequest(POST(AUTH_URL).addHeader(authHeader).withEntity(TOKEN_REQ_ENTITY), materializer).thenCompose(
                   this::unmarshalToTokenResponse).thenAccept(this::setToken);
    }

    private CompletionStage<TokenResponse> unmarshalToTokenResponse(HttpResponse response) {
        return unmarshaller(TokenResponse.class).unmarshal(response.entity(), materializer);
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

    private void setToken(TokenResponse token) {
        this.token = token;
    }
}
