package io.dekstroza.github.examples.reactive.github;

import io.reactivex.schedulers.Schedulers;

import static io.dekstroza.github.examples.reactive.github.GithubApi.createGithubApi;
import static io.dekstroza.github.examples.reactive.github.TwitterApi.createTwitterReactiveApi;

public class Application3 {
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
                .groupBy(t ->t.first)
                .subscribe(result -> {
                    result  .map(s -> s.second)
                            .toList()
                            .subscribe(s -> System.out.println(result.getKey()+" -> "+s));
                });
    }
}
