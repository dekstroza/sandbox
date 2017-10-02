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

        //deki();
        mare();
        Thread.sleep(15000);
    }

    public static void deki() throws IOException {
        createGithubApi().queryGithubProjects("reactive").subscribe(gitHubProject -> {
            log.info("{}", new Summary(gitHubProject, createTwitterReactiveApi().searchTweets(gitHubProject).toList().blockingGet()));
        });
    }

    public static void mare() throws IOException {
        createGithubApi().queryGithubProjects("reactive").take(10).observeOn(Schedulers.io()).flatMap(
                   project -> createTwitterReactiveApi().searchTweets(project).take(10).map(Tweet::getText)
                              .map(tweet -> new Tuple<>(project.getProjectName(), tweet))).groupBy(t -> t.first).subscribe(result -> {
            result.map(s -> s.second).toList().subscribe(s -> System.out.println(result.getKey() + " -> " + s));
        });
    }

    public static void mare2() throws IOException {
        createGithubApi().queryGithubProjects("reactive").take(10).observeOn(Schedulers.io()).flatMap(
                   project -> createTwitterReactiveApi().searchTweets(project).take(10).map(Tweet::getText)
                              .map(tweet -> new Tuple<>(project.getProjectName(), tweet))).groupBy(t -> t.first).subscribe(result -> {
            result.map(s -> s.second).toList().subscribe(s -> System.out.println(result.getKey() + " -> " + s));
        });
    }

}