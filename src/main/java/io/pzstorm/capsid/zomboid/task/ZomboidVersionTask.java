
package io.pzstorm.capsid.zomboid.task;

import io.pzstorm.capsid.CapsidPlugin;
import io.pzstorm.capsid.CapsidTask;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import io.pzstorm.capsid.zomboid.ZomboidTasks;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.*;

/** This task saves and prints Project Zomboid game version. */
public abstract class ZomboidVersionTask extends DefaultTask implements CapsidTask {

    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    public abstract DirectoryProperty getClasspathRoot();

    @OutputFile
    public abstract RegularFileProperty getVersionFile();

    @Override
    public void configure(String group, String description, Project project) {
        CapsidTask.super.configure(group, description, project);

        dependsOn(project.getTasks().getByName(ZomboidTasks.ZOMBOID_CLASSES.name));

        getClasspathRoot()
                .convention(project.getLayout().getBuildDirectory().dir("classes/zomboid"));

        getVersionFile()
                .convention(project.getLayout().getBuildDirectory().file("zomboid-version.txt"));
    }

    @TaskAction
    void execute() {
        File classpathDir = getClasspathRoot().get().getAsFile();

        try {
            URL[] urls = new URL[] {classpathDir.toURI().toURL()};

            try (URLClassLoader loader =
                    new URLClassLoader(urls, this.getClass().getClassLoader())) {
                Class<?> coreClass = loader.loadClass("zombie.core.Core");
                Method getInstanceMethod = coreClass.getMethod("getInstance");
                Object coreInstance = getInstanceMethod.invoke(null);

                Method getVersionMethod = coreInstance.getClass().getMethod("getGameVersion");
                String gameVersion = getVersionMethod.invoke(coreInstance).toString();

                CapsidPlugin.LOGGER.lifecycle("game version " + gameVersion);

                File outputFile = getVersionFile().get().getAsFile();
                Files.writeString(outputFile.toPath(), gameVersion, StandardCharsets.UTF_8);
                CapsidPlugin.LOGGER.lifecycle("Wrote version file to " + outputFile.toPath());
            }

        } catch (Exception e) {
            getLogger().error("Failed to load game version from zomboid classes", e);
            throw new RuntimeException(e);
        }
    }
}
