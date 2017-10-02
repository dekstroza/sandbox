package io.dekstroza.github.examples.reactive.github;

import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static io.dekstroza.github.examples.reactive.github.GithubApi.createGithubApi;
import static io.dekstroza.github.examples.reactive.github.TwitterApi.createTwitterReactiveApi;

public class Application2 {

    public static void main(String[] args) throws Exception {
        createGithubApi()
                .queryGithubProjects("reactive")
                .take(10)
                .observeOn(Schedulers.io())
                .flatMap( project -> createTwitterReactiveApi()
                        .searchTweets(project)
                        .take(10)
                        .map(Tweet::getText)
                        .map(tweet -> new Tuple<>(project.getProjectName(), tweet)))
                .subscribe(result -> System.out.println(result.first+ " -> "+result.second));
    }
}
