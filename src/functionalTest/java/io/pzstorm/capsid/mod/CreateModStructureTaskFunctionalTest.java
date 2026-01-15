
package io.pzstorm.capsid.mod;

import io.pzstorm.capsid.CapsidPlugin;
import io.pzstorm.capsid.PluginFunctionalTest;
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

class CreateModStructureTaskFunctionalTest extends PluginFunctionalTest {

    @Test
    void shouldCreateCorrectModStructureFromSourceSet() {
        BuildResult result = getRunner().withArguments(ModTasks.CREATE_MOD_STRUCTURE.name).build();
        assertTaskOutcomeSuccess(result, ModTasks.CREATE_MOD_STRUCTURE.name);

        Set<String> expectedDirNames = new HashSet<>();
        File gameDir = new File(CapsidPlugin.getGameDirProperty(getProject()), "media");
        Arrays.stream(Objects.requireNonNull(gameDir.listFiles(File::isDirectory)))
                .forEach(f -> expectedDirNames.add(f.getName()));

        Set<String> actualDirNames = new HashSet<>();
        File projectGameDir = new File(getProjectDir(), "media");
        Arrays.stream(Objects.requireNonNull(projectGameDir.listFiles(File::isDirectory)))
                .forEach(f -> actualDirNames.add(f.getName()));

        Assertions.assertEquals(expectedDirNames, actualDirNames);
    }

    @Test
    @SuppressWarnings("SpellCheckingInspection")
    void shouldExcludedResourceSrcDirsFromModStructure() throws IOException {

        Set<String> excludedSrcDirs =
                new HashSet<>(
                        Arrays.asList("media/luaexamples", "media/newuitests", "media/launcher"));
        writeToProjectFile(
                "build.gradle",
                new String[] {
                    "plugins {",
                    "	id('io.pzstorm.capsid')",
                    "}",
                    "",
                    "capsid.excludeResourceDirs "
                            + String.format("\t'%s'", String.join("', '", excludedSrcDirs)),
                });
        File gameDir = CapsidPlugin.getGameDirProperty(getProject());

        for (String excludedSrcDirName : excludedSrcDirs) {
            Files.createDirectory(new File(gameDir, excludedSrcDirName).toPath());
        }
        BuildResult result = getRunner().withArguments(ModTasks.CREATE_MOD_STRUCTURE.name).build();
        assertTaskOutcomeSuccess(result, ModTasks.CREATE_MOD_STRUCTURE.name);

        Set<String> expectedDirNames = new HashSet<>();
        Arrays.stream(
                        Objects.requireNonNull(
                                new File(gameDir, "media").listFiles(File::isDirectory)))
                .filter(f -> !excludedSrcDirs.contains("media/" + f.getName()))
                .forEach(f -> expectedDirNames.add(f.getName()));

        Set<String> actualDirNames = new HashSet<>();
        File projectGameDir = new File(getProjectDir(), "media");
        Arrays.stream(Objects.requireNonNull(projectGameDir.listFiles(File::isDirectory)))
                .forEach(f -> actualDirNames.add(f.getName()));

        Assertions.assertEquals(expectedDirNames, actualDirNames);
    }
}
