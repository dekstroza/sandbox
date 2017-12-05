package io.dekstroza.github.examples.twitter;

import akka.http.javadsl.model.FormData;
import akka.http.javadsl.model.RequestEntity;
import akka.japi.Pair;
import com.jayway.jsonpath.Configuration;

import static com.jayway.jsonpath.Option.ALWAYS_RETURN_LIST;

public class Constants {

    public static final Configuration jsonPathConfig = Configuration.builder().options(ALWAYS_RETURN_LIST).build();
    public static final Pair<String, String> TOKEN_REQ_BODY = new Pair<>("grant_type", "client_credentials");
    public static final RequestEntity TOKEN_REQ_ENTITY = FormData.create(TOKEN_REQ_BODY).toEntity();
    public static final String AUTH_URL = "https://api.twitter.com/oauth2/token";
    public static final String SEARCH_URL = "https://api.twitter.com/1.1/search/tweets.json?q=%s";
    public static final String ENCODING = "UTF-8";
    public static final String TWEETS_QUERY = "$.statuses[*].text";
}
