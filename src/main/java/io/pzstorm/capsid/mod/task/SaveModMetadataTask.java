package io.pzstorm.capsid.mod.task;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.mod.ModProperties;
import io.pzstorm.capsid.zomboid.ZomboidTasks;
import java.io.File;
import java.io.IOException;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

/** This task will save mod metadata to file. */
public class SaveModMetadataTask extends DefaultTask implements CapsidTask {

    @Override
    public void configure(String group, String description, Project project) {
        CapsidTask.super.configure(group, description, project);
        dependsOn(project.getTasks().getByName(ZomboidTasks.ZOMBOID_VERSION.name));
    }

    @TaskAction
    void execute() throws IOException {

        Project project = getProject();

        // ensure that mod.info file exists before writing to it
        File metadataFile = ModProperties.get().getFile(project);
        if (!metadataFile.exists()) {
            // make sure directory structure is prepared
            //noinspection ResultOfMethodCallIgnored
            metadataFile.getParentFile().mkdirs();

            if (!metadataFile.createNewFile()) {
                throw new IOException(
                        "Unable to create mod.info file '" + metadataFile.getPath() + '\'');
            }
        }
        // save mod properties to mod.info file
        ModProperties.get().writeToFile(project);
    }
}
