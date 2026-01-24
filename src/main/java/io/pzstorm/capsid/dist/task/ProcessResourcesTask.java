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
import org.gradle.api.tasks.SourceSet;
import org.gradle.language.jvm.tasks.ProcessResources;

/** This task will copy mod resources to build directory. */
public abstract class ProcessResourcesTask extends ProcessResources implements CapsidTask {

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
        Map<Path, String> map =
                DistributionUtils.getPathsRelativeToModule(module, media.getResources());

        from(media.getResources());
        into(ProjectProperty.MEDIA_RESOURCES_DIR.get(project));

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
        project.getTasks().getByName("processResources").dependsOn(this);
    }
}
