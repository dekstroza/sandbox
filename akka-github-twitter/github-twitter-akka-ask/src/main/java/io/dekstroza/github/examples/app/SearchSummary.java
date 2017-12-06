package io.dekstroza.github.examples.app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.dekstroza.github.examples.github.GitHubProject;
import io.dekstroza.github.examples.twitter.Tweet;

import java.util.List;

@JsonPropertyOrder({ "project", "tweets" })
public class SearchSummary {

    final private GitHubProject gitHubProject;
    final private List<Tweet> tweetList;

    @JsonCreator
    public SearchSummary(GitHubProject gitHubProject, List<Tweet> tweetList) {
        this.gitHubProject = gitHubProject;
        this.tweetList = tweetList;
    }

    @JsonProperty("project")
    public GitHubProject getGitHubProject() {
        return gitHubProject;
    }

    @JsonProperty("tweets")
    public List<Tweet> getTweetList() {
        return tweetList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        SearchSummary that = (SearchSummary) o;

        if (!getGitHubProject().equals(that.getGitHubProject()))
            return false;
        return getTweetList().equals(that.getTweetList());
    }

    @Override
    public int hashCode() {
        int result = getGitHubProject().hashCode();
        result = 31 * result + getTweetList().hashCode();
        return result;
    }
}
