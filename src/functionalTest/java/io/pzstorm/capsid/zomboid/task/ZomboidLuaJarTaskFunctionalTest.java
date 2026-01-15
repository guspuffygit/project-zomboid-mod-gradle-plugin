
package io.pzstorm.capsid.zomboid.task;

import io.pzstorm.capsid.PluginFunctionalTest;
import io.pzstorm.capsid.ProjectProperty;
import io.pzstorm.capsid.util.Utils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import org.gradle.api.Project;
import org.gradle.testkit.runner.BuildResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ZomboidLuaJarTaskFunctionalTest extends PluginFunctionalTest {

    @Test
    void shouldAssembleJarWithCompiledLuaClasses() throws IOException {

        Project project = getProject();
        File zDocLuaDir = ProjectProperty.ZDOC_LUA_DIR.get(project);
        File destination = project.file("lib");

        String[] expectedFiles = new String[] {"luaFile1.lua", "luaFile2.lua", "luaFile3.lua"};
        // create directory structure before creating files
        Assertions.assertTrue(zDocLuaDir.mkdirs());

        // create expected files in lua directory
        for (String expectedFile : expectedFiles) {
            Assertions.assertTrue(new File(zDocLuaDir, expectedFile).createNewFile());
        }
        // create directory structure before walking
        Assertions.assertTrue(destination.mkdir());

        // assert no files present in destination
        Assertions.assertEquals(0, Objects.requireNonNull(destination.listFiles()).length);

        BuildResult result = getRunner().withArguments("zomboidLuaJar").build();
        assertTaskOutcomeSuccess(result, "zomboidLuaJar");

        // confirm archive was created
        File archive = new File(destination, "zdoc-lua.jar");
        Assertions.assertTrue(archive.exists());

        // assert only jar file present in destination directory
        Assertions.assertEquals(1, Objects.requireNonNull(destination.listFiles()).length);

        Utils.unzipArchive(archive, destination);

        Path manifest = new File(destination, "META-INF").toPath();
        Utils.deleteDirectory(manifest.toFile());
        Assertions.assertTrue(archive.delete());

        // assert only expected files are in directory
        Assertions.assertEquals(
                expectedFiles.length, Objects.requireNonNull(destination.listFiles()).length);
        for (String expectedFile : expectedFiles) {
            Assertions.assertTrue(new File(destination, expectedFile).exists());
        }
    }
}
