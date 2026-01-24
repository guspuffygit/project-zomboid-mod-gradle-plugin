package io.pzstorm.capsid.dist;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.pzstorm.capsid.PluginIntegrationTest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DistributionUtilsIntegrationTest extends PluginIntegrationTest {

    @Test
    void shouldGetSourcePathsRelativeToModule() throws IOException {

        Project project = getProject(false);
        project.getPluginManager().apply("java");
        JavaPluginExtension java = project.getExtensions().getByType(JavaPluginExtension.class);
        SourceSet media = java.getSourceSets().create("media");

        media.getJava().setSrcDirs(Collections.singletonList("media/lua"));
        File module = project.file("media");

        String[] filesToCreate =
                new String[] {
                    "media/lua/client/mainClient.lua", "media/lua/server/mainServer.lua",
                };
        for (String filePath : filesToCreate) {
            File projectFile = project.file(filePath);
            File parentFile = projectFile.getParentFile();
            if (!parentFile.exists()) {
                Assertions.assertTrue(parentFile.mkdirs());
            }
            Assertions.assertTrue(projectFile.createNewFile());
        }
        Map<Path, String> expectedSourceResult =
                ImmutableMap.of(
                        Paths.get("client/mainClient.lua"), "lua",
                        Paths.get("server/mainServer.lua"), "lua");
        Assertions.assertEquals(
                expectedSourceResult,
                DistributionUtils.getPathsRelativeToModule(module, media.getJava()));
    }

    @Test
    void shouldGetResourcePathsRelativeToModule() throws IOException {

        Project project = getProject(false);
        project.getPluginManager().apply("java");
        JavaPluginExtension java = project.getExtensions().getByType(JavaPluginExtension.class);
        SourceSet media = java.getSourceSets().create("media");

        media.getResources().setSrcDirs(ImmutableList.of("media/models", "media/maps"));
        File module = project.file("media");

        String[] filesToCreate =
                new String[] {"media/models/testModel.obj", "media/maps/testMap.map"};
        for (String filePath : filesToCreate) {
            File projectFile = project.file(filePath);
            File parentFile = projectFile.getParentFile();
            if (!parentFile.exists()) {
                Assertions.assertTrue(parentFile.mkdirs());
            }
            Assertions.assertTrue(projectFile.createNewFile());
        }
        Map<Path, String> expectedResourcesResult =
                ImmutableMap.of(
                        Paths.get("testModel.obj"), "models",
                        Paths.get("testMap.map"), "maps");
        Assertions.assertEquals(
                expectedResourcesResult,
                DistributionUtils.getPathsRelativeToModule(module, media.getResources()));
    }
}
