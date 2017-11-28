package io.dekstroza.github.examples.app;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.pattern.PatternsCS;
import akka.routing.RoundRobinPool;
import akka.util.Timeout;
import io.dekstroza.github.examples.github.GitHubProject;
import io.dekstroza.github.examples.github.GithubActor;
import io.dekstroza.github.examples.twitter.Tweet;
import io.dekstroza.github.examples.twitter.TwitterActor;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static akka.actor.Props.create;
import static akka.dispatch.Futures.sequence;
import static akka.pattern.Patterns.ask;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static scala.reflect.ClassTag.apply;

public class SearchActor extends AbstractActor {

    protected final ExecutionContextExecutor dispatcher = context().dispatcher();
    private ActorRef twitterActor = getContext().actorOf(create(TwitterActor.class).withRouter(new RoundRobinPool(5)), "twitter-crawlers");
    private ActorRef githubActor = getContext().actorOf(create(GithubActor.class).withRouter(new RoundRobinPool(5)), "github-crawlers");

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(String.class, searchTerm -> Patterns.pipe(performSearch(searchTerm), dispatcher).to(getSender())).build();
    }

    protected Future<Iterable<SearchSummary>> performSearch(String searchTerm) {
        final Future<List<GitHubProject>> searchGithubFuture = searchGithub(searchTerm);
        return searchGithubFuture.map(projectList -> projectList.stream().map(project -> {
            final Future<List<Tweet>> tweetsFuture = searchTwitter(project);
            return tweetsFuture.map(tweets -> new SearchSummary(project, tweets), dispatcher);
        }).collect(toList()), dispatcher).flatMap(v1 -> sequence(v1, dispatcher), dispatcher);
    }

    protected CompletionStage<Iterable<SearchSummary>> performSearchCF(String searchTerm) {
        final CompletionStage<List<GitHubProject>> searchGithubFuture = searchGithubCF(searchTerm);
        return searchGithubFuture.thenApply(projectList -> projectList.stream().map(project -> {
            final CompletableFuture<List<Tweet>> tweetsFuture = searchTwitterCF(project).toCompletableFuture();
            return tweetsFuture.thenApply(tweets -> new SearchSummary(project, tweets));
        }).collect(toList())).thenCompose(this::allAsList);
    }

    private Future<List<GitHubProject>> searchGithub(String searchTerm) {
        return ask(githubActor, searchTerm, defaultTimeout(5)).mapTo(apply(List.class));
    }

    private CompletionStage<List<GitHubProject>> searchGithubCF(String searchTerm) {
        return PatternsCS.ask(githubActor, searchTerm, defaultTimeout(5)).thenApply(List.class::cast);
    }

    private CompletionStage<List<Tweet>> searchTwitterCF(GitHubProject project) {
        return PatternsCS.ask(twitterActor, project.getName(), defaultTimeout(5)).thenApply(List.class::cast);
    }

    private Future<List<Tweet>> searchTwitter(GitHubProject project) {
        return ask(twitterActor, project.getName(), defaultTimeout(5)).mapTo(apply(List.class));
    }

    private Timeout defaultTimeout(long seconds) {
        return Timeout.durationToTimeout(FiniteDuration.apply(seconds, SECONDS));
    }

    public <T> CompletableFuture<Iterable<T>> allAsList(final List<CompletableFuture<T>> futures) {
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).thenApply(
                   ignored -> futures.stream().map(CompletableFuture::join).collect(toList()));
    }
}
