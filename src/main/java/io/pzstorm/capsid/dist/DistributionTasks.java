package io.pzstorm.capsid.dist;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.dist.task.GenerateChangelogTask;
import io.pzstorm.capsid.dist.task.MediaClassesTask;
import io.pzstorm.capsid.dist.task.ProcessResourcesTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;

public enum DistributionTasks {
    GENERATE_CHANGELOG(
            GenerateChangelogTask.class, "generateChangelog", "Generate a project changelog."),
    MEDIA_CLASSES(MediaClassesTask.class, "mediaClasses", "Assembles mod Lua classes.", true),
    PROCESS_RESOURCES(
            ProcessResourcesTask.class, "processMediaResources", "Process mod resources.", true);
    public final String name, description;
    private final Class<? extends CapsidTask> type;
    private final boolean overwrite;

    DistributionTasks(
            Class<? extends CapsidTask> type, String name, String description, boolean overwrite) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.overwrite = overwrite;
    }

    DistributionTasks(Class<? extends CapsidTask> type, String name, String description) {
        this(type, name, description, false);
    }

    /**
     * Configure and register this task for the given {@code Project}.
     *
     * @param project {@code Project} register this task.
     */
    public void register(Project project) {
        TaskContainer tasks = project.getTasks();
        if (overwrite) {
            tasks.replace(name, type).configure("distribution", description, project);
        } else tasks.register(name, type, t -> t.configure("distribution", description, project));
    }
}
