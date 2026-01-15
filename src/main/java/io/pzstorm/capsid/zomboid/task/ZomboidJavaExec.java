
package io.pzstorm.capsid.zomboid.task;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.Configurations;
import io.pzstorm.capsid.zomboid.ZomboidTasks;
import org.gradle.api.Project;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.TaskContainer;

public abstract class ZomboidJavaExec extends JavaExec implements CapsidTask {

    @Override
    public void configure(String group, String description, Project project) {
        CapsidTask.super.configure(group, description, project);

        getMainClass().set("io.cocolabs.pz.zdoc.Main");
        classpath(Configurations.ZOMBOID_DOC.resolve(project));

        TaskContainer tasks = project.getTasks();
        dependsOn(
                tasks.getByName(ZomboidTasks.ZOMBOID_CLASSES.name),
                tasks.getByName(ZomboidTasks.ZOMBOID_VERSION.name));
    }
}
