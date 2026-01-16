package io.pzstorm.capsid.zomboid.task;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.ProjectProperty;
import io.pzstorm.capsid.zomboid.ZomboidJar;
import io.pzstorm.capsid.zomboid.ZomboidTasks;
import java.io.File;
import org.gradle.api.Project;
import org.jetbrains.annotations.Nullable;

/** This task assembles a jar containing decompiled game sources. */
public abstract class ZomboidSourcesJarTask extends ZomboidJar implements CapsidTask {

    @Override
    public void configure(String group, String description, Project project) {
        CapsidTask.super.configure(group, description, project);

        File zomboidSourcesDir = ProjectProperty.ZOMBOID_SOURCES_DIR.get(project);
        onlyIf(
                t -> {
                    @Nullable File[] zomboidSources = zomboidSourcesDir.listFiles();
                    return zomboidSourcesDir.exists()
                            && zomboidSources != null
                            && zomboidSources.length > 0;
                });
        from(zomboidSourcesDir);
        getDestinationDirectory().set(new File(project.getProjectDir(), "lib"));

        getArchiveBaseName().set("zomboid");
        getArchiveClassifier().set("sources");

        dependsOn(project.getTasks().getByName(ZomboidTasks.ZOMBOID_CLASSES.name));
    }
}
