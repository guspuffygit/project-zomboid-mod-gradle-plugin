
package io.pzstorm.capsid.zomboid.task;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.property.VersionProperties;
import io.pzstorm.capsid.util.SemanticVersion;
import io.pzstorm.capsid.zomboid.ZomboidTasks;
import io.pzstorm.capsid.zomboid.ZomboidUtils;
import java.util.Objects;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;

/** This task runs {@code ZomboidDoc} to update compiled Lua library. */
public class UpdateZomboidLuaTask extends DefaultTask implements CapsidTask {

    @Override
    public void configure(String group, String description, Project project) {
        CapsidTask.super.configure(group, description, project);

        SemanticVersion lastZomboidDocVer =
                VersionProperties.LAST_ZDOC_VERSION.findProperty(project);
        int compareResult =
                new SemanticVersion.Comparator()
                        .compare(
                                ZomboidUtils.getZomboidDocVersion(project),
                                Objects.requireNonNull(lastZomboidDocVer));
        // skip task if semantic version could not be resolved
        onlyIf(t -> !lastZomboidDocVer.equals(new SemanticVersion("0.0.0")) && compareResult != 0);

        TaskContainer tasks = project.getTasks();
        dependsOn(tasks.getByName(ZomboidTasks.ZOMBOID_VERSION.name));

        // ZomboidDoc version has changed
        if (compareResult != 0) {
            finalizedBy(project.getTasks().getByName(ZomboidTasks.ZOMBOID_LUA_JAR.name));
            dependsOn(
                    tasks.getByName(ZomboidTasks.ANNOTATE_ZOMBOID_LUA.name),
                    tasks.getByName(ZomboidTasks.COMPILE_ZOMBOID_LUA.name));
        }
    }
}
