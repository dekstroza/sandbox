package io.dekstroza.github.examples.reactive.github;

public class GitHubProject {

    private String projectName;
    private String description;

    public GitHubProject(String projectName, String description) {
        this.projectName = projectName;
        this.description = description;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("GitHubProject{");
        sb.append("projectName='").append(projectName).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
