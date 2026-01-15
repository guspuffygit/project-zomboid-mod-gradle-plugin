
package io.pzstorm.capsid.setup.task;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.setup.LocalProperties;
import io.pzstorm.capsid.setup.SetupTasks;
import io.pzstorm.capsid.setup.xml.GradleRunConfig;
import io.pzstorm.capsid.setup.xml.LaunchRunConfig;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import javax.xml.transform.TransformerException;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.os.OperatingSystem;

/**
 * This task will create useful IDEA run configurations.
 *
 * @see LaunchRunConfig
 */
public class CreateRunConfigurationsTask extends DefaultTask implements CapsidTask {

    @Override
    public void configure(String group, String description, Project project) {
        CapsidTask.super.configure(group, description, project);

        dependsOn(project.getTasks().getByName(SetupTasks.SET_GAME_DIRECTORY.name));
    }

    @TaskAction
    void execute() throws IOException, TransformerException {

        Project project = getProject();

        LaunchRunConfig[] launchRunConfigs =
                new LaunchRunConfig[] {
                    LaunchRunConfig.RUN_ZOMBOID, LaunchRunConfig.RUN_ZOMBOID_LOCAL,
                    LaunchRunConfig.DEBUG_ZOMBOID, LaunchRunConfig.DEBUG_ZOMBOID_LOCAL
                };
        for (LaunchRunConfig launchRunConfig : launchRunConfigs) {
            // linux platform requires additional path properties to be set
            if (OperatingSystem.current() == OperatingSystem.LINUX) {
                Path gameDir =
                        Objects.requireNonNull(LocalProperties.GAME_DIR.findProperty(project))
                                .convert();
                launchRunConfig.vmParamBuilder.withJavaLibraryPaths(
                        gameDir.toString(),
                        gameDir.resolve("linux64").toString(),
                        gameDir.resolve("jre64/lib/amd64").toString());
                launchRunConfig.vmParamBuilder.withLwjglLibraryPaths(gameDir.toString());
            }
            // configure and write to file
            launchRunConfig.configure(project).writeToFile();
        }
        GradleRunConfig.SETUP_WORKSPACE.configure(project).writeToFile();
        GradleRunConfig.INITIALIZE_MOD.configure(project).writeToFile();
    }
}
