package io.dekstroza.github.examples.github;

import akka.actor.AbstractActor;
import akka.actor.Cancellable;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.Http;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import io.dekstroza.github.examples.app.Request;
import io.dekstroza.github.examples.common.GitHubProject;
import io.dekstroza.github.examples.common.SearchSummary;
import io.dekstroza.github.examples.common.TwitterSearchResponse;
import io.dekstroza.github.examples.twitter.TwitterSearchActor;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.duration.FiniteDuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static akka.http.javadsl.model.HttpRequest.GET;
import static akka.http.javadsl.unmarshalling.Unmarshaller.entityToString;
import static com.jayway.jsonpath.JsonPath.using;
import static com.jayway.jsonpath.Option.ALWAYS_RETURN_LIST;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;

public class GitHubSearchActor extends AbstractActor {

    private List<GitHubProject> ghResponse;
    private List<SearchSummary> responses = new ArrayList<>();
    private final Request request;
    private static final String GITHUB_SERVER = "https://api.github.com/search/repositories?q=%s";
    private static final String DESC_QUERY = "$.items[?(@.full_name == '%s')].description";
    private static final String PROJECT_NAMES_QUERY = "$.items[*].full_name";
    private static final Configuration jsonPathConfig = Configuration.builder().options(ALWAYS_RETURN_LIST).build();

    private final Http http = Http.get(getContext().getSystem());
    private final ExecutionContextExecutor dispatcher = context().dispatcher();
    private final Materializer materializer = ActorMaterializer.create(context());
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private Cancellable timeout;

    public static class TimeoutMessage {

    }

    public static class SearchTimeout {
        
    }

    private GitHubSearchActor(Request request) {
        this.request = request;
    }

    public static Props props(final Request request) {
        return Props.create(GitHubSearchActor.class, () -> new GitHubSearchActor(request));
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        searchGitHubProjects();
        scheduleSearchTimeout();
    }

    private void scheduleSearchTimeout() {
        this.timeout = getContext().getSystem().scheduler().scheduleOnce(FiniteDuration.create(10, SECONDS), getSelf(), new SearchTimeout(),
                   dispatcher, getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(TwitterSearchResponse.class, this::handleResponse).match(SearchTimeout.class, timeout1 -> handleSearchTimeout())
                   .match(TimeoutMessage.class, this::handleTimeout).build();
    }

    private void handleSearchTimeout() {
        log.error("Error getting responses, timeout happened.");
        this.timeout.cancel();
        request.getCompletionFunction().accept(responses);
        getContext().getParent().tell(new TerminateMeMessage(), self());
    }

    private void handleTimeout(TimeoutMessage timeoutMessage) {
        log.error("Handling timeout, one of the twitter searchers did not return in time.");
        getContext().getParent().tell(timeoutMessage, getSelf());
        this.timeout.cancel();
        getContext().getParent().tell(new TerminateMeMessage(), self());
    }

    private void handleResponse(TwitterSearchResponse response) {
        responses.add(new SearchSummary(response.getRequest(), response.getResults()));
        if (responses.size() == ghResponse.size()) {
            this.timeout.cancel();
            request.getCompletionFunction().accept(responses);
            getContext().getParent().tell(new TerminateMeMessage(), self());
        }
    }

    private void searchGitHubProjects() {
        http.singleRequest(GET(format(GITHUB_SERVER, request.getSearchWord())), materializer).thenCompose(
                   response -> entityToString().unmarshal(response.entity(), materializer)).thenApply(json -> {
            final DocumentContext ctx = using(jsonPathConfig).parse(json);
            return readProjectNames(ctx).stream().limit(10).map(projectName -> toGitHubProject(ctx, projectName)).collect(Collectors.toList());
        }).thenAccept(this::requestTwitterSearch);
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

    private void requestTwitterSearch(List<GitHubProject> gitHubProjects) {
        this.ghResponse = gitHubProjects;
        gitHubProjects.forEach(project -> getContext().actorOf(TwitterSearchActor.props(project)));
    }
}
