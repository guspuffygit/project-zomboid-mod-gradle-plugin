
package io.pzstorm.capsid.zomboid.task;

import io.pzstorm.capsid.CapsidPlugin;
import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.ProjectProperty;
import org.gradle.api.Project;
import org.gradle.api.tasks.Copy;

/** This task will copy {@code zomboidClassesDir} with game install directory. */
public abstract class ZomboidClassesTask extends Copy implements CapsidTask {

    @Override
    public void configure(String group, String description, Project project) {
        CapsidTask.super.configure(group, description, project);

        setIncludeEmptyDirs(false);
        from(CapsidPlugin.getGameDirProperty(project));
        into(ProjectProperty.ZOMBOID_CLASSES_DIR.get(project));
        include("**/*.class", "stdlib.lbc");
    }
}
