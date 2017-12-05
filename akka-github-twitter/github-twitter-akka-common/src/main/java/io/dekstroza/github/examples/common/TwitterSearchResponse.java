package io.dekstroza.github.examples.common;

import java.util.List;
import java.util.Optional;

public class TwitterSearchResponse {

    private final GitHubProject request;
    private final List<Tweet> results;
    private final Optional<Throwable> error;

    public TwitterSearchResponse(List<Tweet> results, GitHubProject request, Optional<Throwable> error) {
        this.results = results;
        this.request = request;
        this.error = error;
    }

    public List<Tweet> getResults() {
        return results;
    }

    public Optional<Throwable> getError() {
        return error;
    }

    public GitHubProject getRequest() {
        return request;
    }
}
