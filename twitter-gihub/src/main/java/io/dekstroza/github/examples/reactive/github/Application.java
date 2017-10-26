package io.dekstroza.github.examples.reactive.github;

import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static io.dekstroza.github.examples.reactive.github.GithubApi.createGithubApi;
import static io.dekstroza.github.examples.reactive.github.TwitterApi.createTwitterReactiveApi;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {

        createSummary();
        Thread.sleep(10000);
    }

    public static void deki() throws IOException {
        createGithubApi().queryGithubProjects("reactive").take(10).subscribe(gitHubProject -> {
            log.info("{}", new Summary(gitHubProject, createTwitterReactiveApi().searchTweets(gitHubProject).toList().blockingGet()));
        });
    }

    public static void createSummary() throws Exception {
        createGithubApi().queryGithubProjects("reactive").take(10).observeOn(Schedulers.io()).flatMap(
                   project -> createTwitterReactiveApi().searchTweets(project)
                              .map(tweet -> new Tuple<>(project, tweet)))
                   .groupBy(t -> t.first)
                   .subscribe(result -> {
            result.map(s -> s.second).toList().subscribe(s -> log.info("{}",new Summary(result.getKey(),s)));
        });
    }

}