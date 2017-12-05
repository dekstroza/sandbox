package io.dekstroza.github.examples.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "name", "description" })
public class GitHubProject {

    private final String name;
    private final String description;

    @JsonCreator
    public GitHubProject(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        GitHubProject that = (GitHubProject) o;

        if (!getName().equals(that.getName()))
            return false;
        return getDescription().equals(that.getDescription());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getDescription().hashCode();
        return result;
    }

}
