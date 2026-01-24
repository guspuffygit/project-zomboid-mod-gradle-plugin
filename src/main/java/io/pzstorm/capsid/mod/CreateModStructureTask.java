package io.pzstorm.capsid.mod;

import io.pzstorm.capsid.CapsidPluginExtension;
import io.pzstorm.capsid.CapsidTask;
import java.io.File;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskAction;

/**
 * This task creates source and resource directories for {@code media} module. Resource directories
 * can be excluded with {@code excludeResourceDirs} plugin configuration.
 *
 * @see CapsidPluginExtension#excludeResourceDirs(String...)
 */
@SuppressWarnings("WeakerAccess")
public class CreateModStructureTask extends DefaultTask implements CapsidTask {

    @TaskAction
    void execute() {
        JavaPluginExtension java =
                getProject().getExtensions().getByType(JavaPluginExtension.class);
        SourceSet media = java.getSourceSets().getByName("media");

        for (File srcDir : media.getJava().getSrcDirs()) {
            if (!srcDir.exists() && !srcDir.mkdirs()) {
                String msg = "Unable to create mod structure for source dir '%s'";
                throw new GradleException(String.format(msg, srcDir.toPath()));
            }
        }
        for (File resDir : media.getResources().getSrcDirs()) {
            if (!resDir.exists() && !resDir.mkdirs()) {
                String msg = "Unable to create mod structure for resource dir '%s'";
                throw new GradleException(String.format(msg, resDir.toPath()));
            }
        }
    }
}
