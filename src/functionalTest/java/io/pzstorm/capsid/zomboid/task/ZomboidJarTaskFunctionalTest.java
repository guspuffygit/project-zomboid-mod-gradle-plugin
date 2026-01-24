package io.pzstorm.capsid.zomboid.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.MoreFiles;
import io.pzstorm.capsid.CapsidPlugin;
import io.pzstorm.capsid.PluginFunctionalTest;
import io.pzstorm.capsid.ProjectProperty;
import io.pzstorm.capsid.util.Utils;
import io.pzstorm.capsid.zomboid.ZomboidTasks;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.gradle.testkit.runner.BuildResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ZomboidJarTaskFunctionalTest extends PluginFunctionalTest {

    @Test
    void shouldAssembleJarArchiveContainingGameClasses() throws IOException {

        File source = CapsidPlugin.getGameDirProperty(getProject());
        Assertions.assertTrue(ProjectProperty.ZOMBOID_CLASSES_DIR.get(getProject()).mkdirs());

        File destination = new File(getProject().getProjectDir(), "lib");
        Assertions.assertTrue(destination.mkdirs());

        Set<String> filesToInclude =
                ImmutableSet.of("class1.class", "class2.class", "class3.class");
        File dummyClass = Utils.getFileFromResources("dummy.class");
        for (String include : filesToInclude) {
            com.google.common.io.Files.copy(dummyClass, new File(source, include));
        }
        Map<String, File> filesToExclude =
                ImmutableMap.of(
                        "textFile.txt", Utils.getFileFromResources("dummy.txt"),
                        "imageFile.png", Utils.getFileFromResources("dummy.png"));
        for (Map.Entry<String, File> entry : filesToExclude.entrySet()) {
            com.google.common.io.Files.copy(entry.getValue(), new File(source, entry.getKey()));
        }
        BuildResult result = getRunner().withArguments(ZomboidTasks.ZOMBOID_JAR.name).build();
        assertTaskOutcomeSuccess(result, ZomboidTasks.ZOMBOID_JAR.name);

        try (Stream<Path> stream = java.nio.file.Files.walk(destination.toPath())) {
            Utils.unzipArchive(
                    stream.filter(f -> MoreFiles.getFileExtension(f).equals("jar"))
                            .findAny()
                            .orElseThrow(RuntimeException::new)
                            .toFile(),
                    destination);
        }
        for (String include : filesToInclude) {
            Assertions.assertTrue(new File(destination, include).exists());
        }
        for (Map.Entry<String, File> entry : filesToExclude.entrySet()) {
            Assertions.assertFalse(new File(destination, entry.getKey()).exists());
        }
    }
}
