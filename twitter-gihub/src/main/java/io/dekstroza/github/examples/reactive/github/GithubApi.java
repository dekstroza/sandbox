package io.dekstroza.github.examples.reactive.github;

import com.jayway.jsonpath.DocumentContext;
import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.jayway.jsonpath.JsonPath.parse;
import static io.reactivex.Observable.create;
import static io.reactivex.Observable.zip;
import static java.lang.String.format;

public class GithubApi {

    private static final Logger logger = LoggerFactory.getLogger(GithubApi.class);
    private static final String GITHUB_SERVER = "api.github.com";
    private static final int DEFAULT_HTTPS_PORT = 443;
    private static final String GITHUB_QUERY_PATH = "/search/repositories";
    public static final String USER_AGENT = "User-Agent";
    public static final String USER_AGENET_VALUE = "dekstroza";
    private static GithubApi instance = new GithubApi();

    private SSLEngine sslEngine;

    private static SSLEngine defaultSSLEngineForClient() throws NoSuchAlgorithmException {
        SSLContext sslCtx = SSLContext.getDefault();
        SSLEngine sslEngine = sslCtx.createSSLEngine(GITHUB_SERVER, DEFAULT_HTTPS_PORT);
        sslEngine.setUseClientMode(true);
        return sslEngine;
    }

    private GithubApi() {
        try {
            sslEngine = defaultSSLEngineForClient();
        } catch (NoSuchAlgorithmException nsae) {
            logger.error("Failed to create SSLEngine.", nsae);
            System.exit(-1);
        }
    }

    public static GithubApi createGithubApi() {
        return instance;
    }

    Observable<GitHubProject> queryGithubProjects(final String keyword) throws IOException {

        OkHttpClient client = new OkHttpClient();
        final Request request = new Builder().url(format("https://%s:%d%s?q=%s", GITHUB_SERVER, DEFAULT_HTTPS_PORT, GITHUB_QUERY_PATH, keyword))
                   .build();
        DocumentContext ctx = parse(client.newCall(request).execute().body().byteStream());

        Observable<String> observableName = create(emitter1 -> {
            try {
                ctx.read("$.items[*].full_name", List.class).parallelStream().forEach(x -> emitter1.onNext((String) x));
                emitter1.onComplete();
            } catch (Exception e) {
                emitter1.onError(e);
            }
        });
        Observable<String> observableDescription = create(emitter2 -> {
            try {
                ctx.read("$.items[*].description", List.class).parallelStream().forEach(x -> emitter2.onNext(x == null ? "" : (String) x));
                emitter2.onComplete();
            } catch (Exception e) {
                emitter2.onError(e);
            }
        });

        return zip(observableName, observableDescription, (s, s2) -> {
            return new GitHubProject(s, s2);
        });

    }

}
