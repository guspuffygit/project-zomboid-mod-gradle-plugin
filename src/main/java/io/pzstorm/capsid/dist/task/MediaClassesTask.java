
package io.pzstorm.capsid.dist.task;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.ProjectProperty;
import io.pzstorm.capsid.dist.DistributionUtils;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.SourceSet;

/** This task assembles mod Lua classes with directory hierarchy ready for distribution. */
public abstract class MediaClassesTask extends Copy implements CapsidTask {

    @Override
    public void configure(String group, String description, Project project) {
        CapsidTask.super.configure(group, description, project);

        JavaPluginExtension java = project.getExtensions().getByType(JavaPluginExtension.class);
        SourceSet media = java.getSourceSets().getByName("media");
        File module = project.file("media");
        if (!module.exists()) {
            //noinspection ResultOfMethodCallIgnored
            module.mkdirs();
        }
        Map<Path, String> map = DistributionUtils.getPathsRelativeToModule(module, media.getJava());

        from(media.getJava().getSrcDirs());
        into(ProjectProperty.MEDIA_CLASSES_DIR.get(project));

        eachFile(
                fcd -> {
                    String path = map.get(Paths.get(fcd.getPath()));
                    if (path == null) {
                        throw new GradleException(
                                "Unable to relativize copy path '" + fcd.getPath() + '\'');
                    }
                    fcd.setRelativePath(fcd.getRelativePath().prepend(path));
                });
        setIncludeEmptyDirs(false);
    }
}
