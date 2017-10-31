package io.dekstroza.examples.vertx;

import com.jayway.jsonpath.JsonPath;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.client.HttpResponse;
import io.vertx.rxjava.ext.web.client.WebClient;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class GithubApi {
    static final String GITHUB_SERVER = "api.github.com";
    static final int DEFAULT_HTTPS_PORT = 443;
    static final String GITHUB_QUERY_PATH = "/search/repositories";
    static final String DESC_QUERY = "$.items[?(@.full_name == '%s')].description";
    private static GithubApi instance;
    private Vertx vertx;

    private GithubApi(Vertx vertx){
        this.vertx = vertx;
    }

    public static GithubApi createGithubApi(Vertx vertx) {
        return instance == null ? (instance = new GithubApi(vertx)) : instance;
    }

    public Observable<GitHubProject>  searchGitHubProjects(final String keyword) {
       return WebClient.create(vertx,
                   new WebClientOptions().setSsl(true).setDefaultPort(DEFAULT_HTTPS_PORT)).get(DEFAULT_HTTPS_PORT, GITHUB_SERVER, format("%s?q=%s", GITHUB_QUERY_PATH, keyword))
                  .rxSend()
                  .map(HttpResponse::bodyAsString)
                  .map(JsonPath::parse)
                  .map(ctx -> {
                        final List<GitHubProject> gitHubProjects = new ArrayList<>();
                        final List<String> projectNames = ctx.read("$.items[*].full_name", List.class);
                        projectNames.forEach(projectName -> {
                            final List<String> projectDescription = ctx.read(format(DESC_QUERY, (String) projectName), List.class);
                            gitHubProjects.add(new GitHubProject(projectName, projectDescription == null ? "" : projectDescription.get(0)));
                        });
            return gitHubProjects;
        }).flatMapObservable(Observable::from);

    }

}
