package io.dekstroza.github.examples.reactive.github;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Summary {
    private static final Logger log = LoggerFactory.getLogger(Summary.class);

    private final GitHubProject project;
    private final List<Tweet> tweetList;

    public Summary(GitHubProject project, List<Tweet> tweetList) {
        this.project = project;
        this.tweetList = tweetList;
    }

    public GitHubProject getProject() {
        return project;
    }

    public List<Tweet> getTweetList() {
        return tweetList;
    }

    @Override
    public String toString() {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        return gson.toJson(this);
    }
}
