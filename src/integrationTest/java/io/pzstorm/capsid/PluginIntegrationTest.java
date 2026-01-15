
package io.pzstorm.capsid;

import io.pzstorm.capsid.util.UnixPath;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import javax.annotation.Nullable;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

@Tag("integration")
public abstract class PluginIntegrationTest {

    private static final File PARENT_TEMP_DIR = new File("build/tmp/integrationTest");

    private Project project;
    private Plugin<Project> plugin;
    private UnixPath gameDir, ideaHome;

    @BeforeEach
    void createProjectAndApplyPlugin() throws IOException {

        File projectDir = generateProjectDirectory();
        Assertions.assertTrue(projectDir.mkdirs());

        File localProperties = new File(projectDir, "local.properties");
        Assertions.assertTrue(localProperties.createNewFile());

        gameDir = UnixPath.get(new File(projectDir, "gameDir").getAbsoluteFile());
        Files.createDirectory(gameDir.convert());

        File gameMediaDir = new File(gameDir.toString(), "media");
        Files.createDirectory(gameMediaDir.toPath());

        for (String dir : new String[] {"lua", "maps", "models"}) {
            Files.createDirectories(new File(gameMediaDir, dir).toPath());
        }
        ideaHome = UnixPath.get(new File(projectDir, "ideaHome").getAbsoluteFile());
        Files.createDirectory(ideaHome.convert());

        Path localPropertiesPath = localProperties.toPath();
        try (Writer writer = Files.newBufferedWriter(localPropertiesPath, StandardCharsets.UTF_8)) {
            writer.write(
                    String.join(
                            "\n",
                            // property values with backslashes are considered malformed
                            "gameDir=" + gameDir.toString(),
                            "ideaHome=" + ideaHome.toString()));
        }
        project = ProjectBuilder.builder().withProjectDir(projectDir).build();
    }

    private File generateProjectDirectory() {
        // generate a directory name that doesn't exist yet
        File result = getRandomProjectDirectory();
        while (result.exists()) {
            result = getRandomProjectDirectory();
        }
        return result;
    }

    private File getRandomProjectDirectory() {
        return new File(PARENT_TEMP_DIR, "test" + new Random().nextInt(1000));
    }

    protected Project getProject(boolean applyPlugin) {
        if (applyPlugin) {
            applyCapsidPlugin();
        }
        return project;
    }

    @SuppressWarnings("unchecked")
    protected void applyCapsidPlugin() {
        plugin = project.getPlugins().apply("io.pzstorm.capsid");
    }

    protected @Nullable Plugin<Project> getPlugin() {
        return plugin;
    }

    protected UnixPath getGameDirPath() {
        return gameDir;
    }

    protected UnixPath getIdeaHomePath() {
        return ideaHome;
    }

    protected void writeToProjectFile(String path, String[] lines) throws IOException {

        File projectFile = new File(project.getProjectDir(), path);
        try (Writer writer =
                Files.newBufferedWriter(projectFile.toPath(), StandardCharsets.UTF_8)) {
            writer.write(String.join("\n", lines));
        }
    }
}
