package io.dekstroza.examples.vertx;

import io.vertx.core.json.Json;

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
        return Json.encodePrettily(this);

    }
}
