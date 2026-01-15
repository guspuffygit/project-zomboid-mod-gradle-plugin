
package io.pzstorm.capsid.dist.task;

import io.pzstorm.capsid.PluginFunctionalTest;
import io.pzstorm.capsid.ProjectProperty;
import io.pzstorm.capsid.dist.DistributionTasks;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.gradle.testkit.runner.BuildResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MediaClassesTaskFunctionalTest extends PluginFunctionalTest {

    @Test
    void shouldAssembleMediaClassesWithCorrectDirectoryStructure() throws IOException {

        String[] filesToCreate =
                new String[] {
                    "lua/client/mainClient.lua",
                    "lua/server/mainServer.lua",
                    "models/testModel.obj",
                    "maps/testMap.map"
                };
        for (String path : filesToCreate) {
            File file = getProject().file("media/" + path);
            File parentFile = file.getParentFile();
            Assertions.assertTrue(parentFile.exists() || parentFile.mkdirs());
            Assertions.assertTrue(file.createNewFile());
        }
        BuildResult result =
                getRunner().withArguments(DistributionTasks.MEDIA_CLASSES.name).build();
        assertTaskOutcomeSuccess(result, DistributionTasks.MEDIA_CLASSES.name);

        File mediaClassesDir = ProjectProperty.MEDIA_CLASSES_DIR.get(getProject());
        String[] expectedFiles =
                new String[] {"lua/client/mainClient.lua", "lua/server/mainServer.lua"};
        try (Stream<Path> stream =
                Files.walk(mediaClassesDir.toPath()).filter(Files::isRegularFile)) {
            Assertions.assertEquals(expectedFiles.length, stream.count());
        }
        for (String expectedFile : expectedFiles) {
            Assertions.assertTrue(new File(mediaClassesDir, expectedFile).exists());
        }
    }
}
