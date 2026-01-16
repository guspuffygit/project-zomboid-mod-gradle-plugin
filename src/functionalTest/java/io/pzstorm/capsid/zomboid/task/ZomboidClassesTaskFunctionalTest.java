package io.pzstorm.capsid.zomboid.task;

import io.pzstorm.capsid.PluginFunctionalTest;
import io.pzstorm.capsid.ProjectProperty;
import io.pzstorm.capsid.zomboid.ZomboidTasks;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.gradle.testkit.runner.BuildResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ZomboidClassesTaskFunctionalTest extends PluginFunctionalTest {

    @Test
    void shouldSyncZomboidClassesFromInstallDirectory() throws IOException {

        File zomboidClassesDir = ProjectProperty.ZOMBOID_CLASSES_DIR.get(getProject());
        Assertions.assertFalse(zomboidClassesDir.exists());

        File gameDir = getGameDirPath().convert().toAbsolutePath().toFile();
        File[] includedFiles =
                new File[] {
                    new File(gameDir, "incFile1.class"),
                    new File(gameDir, "incFile2.class"),
                    new File(gameDir, "incFile3.class"),
                    new File(gameDir, "stdlib.lbc")
                };
        for (File file : includedFiles) {
            Assertions.assertTrue(file.createNewFile());
        }
        File[] excludedFiles =
                new File[] {new File(gameDir, "excFile1.txt"), new File(gameDir, "excFile2.png")};
        for (File file : excludedFiles) {
            Assertions.assertTrue(file.createNewFile());
        }
        File excludedDir = new File(gameDir, "excludedDir");
        Files.createDirectory(excludedDir.toPath());

        BuildResult result = getRunner().withArguments(ZomboidTasks.ZOMBOID_CLASSES.name).build();
        assertTaskOutcomeSuccess(result, ZomboidTasks.ZOMBOID_CLASSES.name);

        // class files that were synced from install directory
        Set<File> zomboidClasses =
                new HashSet<>(Arrays.asList(Objects.requireNonNull(zomboidClassesDir.listFiles())));
        Assertions.assertEquals(includedFiles.length, zomboidClasses.size());

        // expect finding all included files
        for (File includedFile : includedFiles) {
            File targetFile = new File(zomboidClassesDir, includedFile.getName());
            Assertions.assertTrue(zomboidClasses.contains(targetFile));
        }
        // expect NOT finding any excluded files
        for (File excludedFile : excludedFiles) {
            File targetFile = new File(zomboidClassesDir, excludedFile.getName());
            Assertions.assertFalse(zomboidClasses.contains(targetFile));
        }
        // expect NOT finding excluded directory
        Assertions.assertFalse(zomboidClasses.contains(excludedDir));
    }
}
