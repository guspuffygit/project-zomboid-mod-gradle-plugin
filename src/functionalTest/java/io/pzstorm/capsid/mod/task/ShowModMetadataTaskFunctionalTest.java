
package io.pzstorm.capsid.mod.task;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import io.pzstorm.capsid.PluginFunctionalTest;
import io.pzstorm.capsid.mod.ModTasks;
import java.io.IOException;
import java.util.List;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ShowModMetadataTaskFunctionalTest extends PluginFunctionalTest {

    @Test
    void shouldPrintMetadataInformationToStreamOutput() throws IOException {
        GradleRunner runner = getRunner();
        writeToProjectFile(
                "mod.info",
                new String[] {
                    "name=TestMod",
                    "description=",
                    "url=https://github.com/test/mod",
                    "id=" + runner.getProjectDir().getName(),
                    "modversion=1.0.5",
                    "pzversion=41.50-IWBUMS"
                });

        BuildResult result = runner.withArguments(ModTasks.SHOW_MOD_METADATA.name).build();
        assertTaskOutcomeSuccess(result, ModTasks.SHOW_MOD_METADATA.name);

        List<String> expectedOutput =
                ImmutableList.of(
                        "This is a mod for Project Zomboid 41.50-IWBUMS",
                        "------------------------------------------------",
                        "Name: TestMod",
                        "Description: <not specified>",
                        "URL: https://github.com/test/mod",
                        "ID: " + runner.getProjectDir().getName(),
                        "Version: 1.0.5");

        List<String> actualOutput =
                Splitter.on(System.lineSeparator())
                        .trimResults()
                        .omitEmptyStrings()
                        .splitToList(result.getOutput());

        int startIndex = actualOutput.indexOf(expectedOutput.get(0));

        Assertions.assertTrue(
                startIndex >= 0, "Could not find start of metadata output in Gradle logs");

        List<String> relevantOutput =
                actualOutput.subList(startIndex, startIndex + expectedOutput.size());

        Assertions.assertEquals(expectedOutput, relevantOutput);
    }
}
