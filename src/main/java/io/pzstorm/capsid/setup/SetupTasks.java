package io.pzstorm.capsid.setup;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.setup.task.CreateDiscordIntegrationTask;
import io.pzstorm.capsid.setup.task.CreateRunConfigurationsTask;
import io.pzstorm.capsid.setup.task.CreateSearchScopesTask;
import io.pzstorm.capsid.setup.task.setGameDirectoryTask;
import org.gradle.api.Project;

/** Tasks that help setup modding work environment. */
public enum SetupTasks {
    SET_GAME_DIRECTORY(
            setGameDirectoryTask.class, "setGameDirectory", "Set game directory via user input."),
    CREATE_RUN_CONFIGS(
            CreateRunConfigurationsTask.class,
            "createRunConfigurations",
            "Create useful IDEA run configurations."),
    CREATE_SEARCH_SCOPES(
            CreateSearchScopesTask.class,
            "createSearchScopes",
            "Create IDEA search scopes for project files."),
    CREATE_DISCORD_INTEGRATION(
            CreateDiscordIntegrationTask.class,
            "createDiscordIntegration",
            "Show IDEA project in Discord via rich presence.");
    public final String name, description;
    private final Class<? extends CapsidTask> type;

    SetupTasks(Class<? extends CapsidTask> type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    /**
     * Configure and register this task for the given {@code Project}.
     *
     * @param project {@code Project} register this task.
     */
    public void register(Project project) {
        project.getTasks()
                .register(name, type, t -> t.configure("build setup", description, project));
    }
}
