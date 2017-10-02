package io.dekstroza.github.examples.reactive.github;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static io.dekstroza.github.examples.reactive.github.GithubApi.createGithubApi;
import static io.dekstroza.github.examples.reactive.github.TwitterApi.createTwitterReactiveApi;
import static io.reactivex.Observable.zip;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {
        final Observable<GitHubProject> gitHubProjectObservable = createGithubApi().queryGithubProjects("reactive");
        final Observable<Single<List<Tweet>>> tweetListObservable = gitHubProjectObservable.take(10).map(
                   gitHubProject -> createTwitterReactiveApi().searchTweets(gitHubProject).toList());

        zip(gitHubProjectObservable, tweetListObservable, (gitHubProject, tweets) -> {
            return new Summary(gitHubProject, tweets.blockingGet());
        }).forEach(summary -> log.info("{}", summary));
    }
}
