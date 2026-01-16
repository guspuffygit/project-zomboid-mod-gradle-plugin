package io.pzstorm.capsid.zomboid.task;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.ProjectProperty;
import io.pzstorm.capsid.zomboid.ZomboidJar;
import io.pzstorm.capsid.zomboid.ZomboidTasks;
import java.io.File;
import org.gradle.api.Project;
import org.jetbrains.annotations.Nullable;

/** This task assembles a jar archive containing game classes. */
public abstract class ZomboidJarTask extends ZomboidJar implements CapsidTask {

    @Override
    public void configure(String group, String description, Project project) {
        CapsidTask.super.configure(group, description, project);

        File zomboidClassesDir = ProjectProperty.ZOMBOID_CLASSES_DIR.get(project);
        onlyIf(
                t -> {
                    @Nullable File[] zomboidClasses = zomboidClassesDir.listFiles();
                    return zomboidClassesDir.exists()
                            && zomboidClasses != null
                            && zomboidClasses.length > 0;
                });
        getDestinationDirectory().set(new File(project.getProjectDir(), "lib"));
        from(zomboidClassesDir);

        setIncludeEmptyDirs(false);
        getArchiveBaseName().set("zomboid");

        dependsOn(project.getTasks().getByName(ZomboidTasks.ZOMBOID_CLASSES.name));
    }
}
