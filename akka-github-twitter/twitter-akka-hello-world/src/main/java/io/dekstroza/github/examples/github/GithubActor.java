package io.dekstroza.github.examples.github;

import akka.actor.AbstractActor;
import akka.http.javadsl.Http;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import scala.compat.java8.FutureConverters;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.Future;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static akka.http.javadsl.model.HttpRequest.GET;
import static akka.http.javadsl.unmarshalling.Unmarshaller.entityToString;
import static com.jayway.jsonpath.JsonPath.using;
import static com.jayway.jsonpath.Option.ALWAYS_RETURN_LIST;
import static java.lang.String.format;

public class GithubActor extends AbstractActor {

    private static final String GITHUB_SERVER = "https://api.github.com/search/repositories?q=%s";
    private static final String DESC_QUERY = "$.items[?(@.full_name == '%s')].description";
    private static final String PROJECT_NAMES_QUERY = "$.items[*].full_name";
    private static final Configuration jsonPathConfig = Configuration.builder().options(ALWAYS_RETURN_LIST).build();

    final Http http = Http.get(getContext().getSystem());
    private final ExecutionContextExecutor dispatcher = context().dispatcher();
    private final Materializer materializer = ActorMaterializer.create(context());

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(String.class, searchRequest -> Patterns.pipe(searchGitHubProjects(searchRequest), dispatcher).to(sender()))
                   .build();
    }

    private Future<List<GitHubProject>> searchGitHubProjects(String searchRequest) {
        return FutureConverters.toScala(http.singleRequest(GET(format(GITHUB_SERVER, searchRequest)), materializer)
                   .thenCompose(response -> entityToString().unmarshal(response.entity(), materializer)).thenApply(json -> {
                       final DocumentContext ctx = using(jsonPathConfig).parse(json);
                       return readProjectNames(ctx).stream().limit(10).parallel().map(projectName -> toGitHubProject(ctx, projectName)).collect(
                                  Collectors.toList());
                   }));
    }

    private CompletionStage<List<GitHubProject>> searchGitHubProjectsWithCS(String searchRequest) {
        return http.singleRequest(GET(format(GITHUB_SERVER, searchRequest)), materializer).thenCompose(
                   response -> entityToString().unmarshal(response.entity(), materializer)).thenApply(json -> {
            final DocumentContext ctx = using(jsonPathConfig).parse(json);
            return readProjectNames(ctx).stream().limit(10).parallel().map(projectName -> toGitHubProject(ctx, projectName)).collect(
                       Collectors.toList());
        });
    }

    private GitHubProject toGitHubProject(final DocumentContext ctx, String projectName) {
        return new GitHubProject(projectName, getDescription(readDescriptions(ctx, projectName)));
    }

    private List<String> readProjectNames(final DocumentContext ctx) {
        return ctx.read(PROJECT_NAMES_QUERY);
    }

    private List<String> readDescriptions(final DocumentContext ctx, String projectName) {
        return ctx.read(format(DESC_QUERY, projectName));
    }

    private String getDescription(List<String> descriptions) {
        return descriptions.stream().filter(Objects::nonNull).findFirst().orElse("");
    }
}
