package io.dekstroza.github.examples.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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

        return !getGitHubProject().equals(that.getGitHubProject()) ? false : getTweetList().equals(that.getTweetList());
    }

    @Override
    public int hashCode() {
        int result = getGitHubProject().hashCode();
        result = 31 * result + getTweetList().hashCode();
        return result;
    }
}
