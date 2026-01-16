package io.pzstorm.capsid.dist.task;

import io.pzstorm.capsid.PluginFunctionalTest;
import io.pzstorm.capsid.dist.DistributionTasks;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProcessResourcesTaskFunctionalTest extends PluginFunctionalTest {

    @BeforeEach
    void createSourceAndResourceFiles() throws IOException {

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
    }

    @Test
    void shouldProcessModResourcesWithCorrectDirectoryStructure() throws IOException {

        GradleRunner runner = getRunner();
        BuildResult result = runner.withArguments(DistributionTasks.PROCESS_RESOURCES.name).build();
        assertTaskOutcomeSuccess(result, DistributionTasks.PROCESS_RESOURCES.name);

        File resourcesDir = new File(runner.getProjectDir(), "build/resources/media");
        String[] expectedFiles = new String[] {"models/testModel.obj", "maps/testMap.map"};
        try (Stream<Path> stream = Files.walk(resourcesDir.toPath()).filter(Files::isRegularFile)) {
            Assertions.assertEquals(expectedFiles.length, stream.count());
        }
        for (String expectedFile : expectedFiles) {
            Assertions.assertTrue(new File(resourcesDir, expectedFile).exists());
        }
    }

    @Test
    void whenRunningProcessResourcesShouldDependOnThisTask() {
        BuildResult result = getRunner().withArguments("processResources").build();
        assertTaskOutcome(result, "processResources", TaskOutcome.NO_SOURCE);
        assertTaskOutcomeSuccess(result, DistributionTasks.PROCESS_RESOURCES.name);
    }
}
