
package io.pzstorm.capsid;

import io.pzstorm.capsid.util.UnixPath;
import io.pzstorm.capsid.util.Utils;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Random;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

@Tag("functional")
public abstract class PluginFunctionalTest {

    private static final File PARENT_TEMP_DIR = new File("build/tmp/functionalTest");

    private final String projectName;

    private Project project;
    private File projectDir;
    private UnixPath gameDir;
    private CapsidGradleRunner runner;

    protected PluginFunctionalTest() {
        this.projectDir = generateProjectDirectory();
        this.projectName = projectDir.getName();
    }

    protected PluginFunctionalTest(String projectName) {
        this.projectName = projectName;
    }

    protected static void assertTaskOutcome(
            BuildResult result, String taskName, TaskOutcome outcome) {
        BuildTask task = Objects.requireNonNull(result.task(':' + taskName));
        Assertions.assertEquals(outcome, task.getOutcome());
    }

    protected static void assertTaskOutcomeSuccess(BuildResult result, String taskName) {
        assertTaskOutcome(result, taskName, TaskOutcome.SUCCESS);
    }

    @BeforeEach
    void createRunner() throws IOException {

        if (projectDir == null) {
            projectDir = new File(PARENT_TEMP_DIR, projectName);
        }
        // make sure the project directory doesn't exist
        if (projectDir.exists()) {
            Utils.deleteDirectory(projectDir);
        }
        // Setup the test build
        Files.createDirectories(projectDir.toPath());
        writeToProjectFile(
                "settings.gradle",
                new String[] {String.format("rootProject.name = '%s'", projectName)});
        writeToProjectFile(
                "build.gradle", new String[] {"plugins {", "	id('io.pzstorm.capsid')", "}"});
        runner = CapsidGradleRunner.create();

        // configure gradle runner
        runner.forwardOutput();
        runner.withPluginClasspath();
        runner.withProjectDir(projectDir);
        runner.withDebug(true);

        gameDir = UnixPath.get(new File(projectDir, "gameDir"));
        Files.createDirectory(gameDir.convert());

        File gameMediaDir = new File(gameDir.convert().toFile(), "media");
        Files.createDirectory(gameMediaDir.toPath());

        for (String dir : new String[] {"lua", "maps", "models"}) {
            Path createDir = new File(gameMediaDir, dir).toPath().toAbsolutePath();
            Files.createDirectory(createDir);
            Assertions.assertTrue(createDir.toFile().exists());
        }
        // add project properties
        //noinspection SpellCheckingInspection
        runner.withArguments("-PgameDir=" + gameDir.toString());
    }

    private Project initializeProject() {
        this.project = ProjectBuilder.builder().withProjectDir(projectDir).build();
        ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();
        ext.set("gameDir", gameDir.toString());
        return project;
    }

    protected Project getProject() {
        return project != null ? project : initializeProject();
    }

    protected File getProjectDir() {
        return getProject().getProjectDir();
    }

    protected CapsidGradleRunner getRunner() {
        return runner;
    }

    protected UnixPath getGameDirPath() {
        return gameDir;
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

    protected void writeToProjectFile(String path, String[] lines) throws IOException {

        Path projectFilePath = new File(projectDir, path).toPath();
        try (Writer writer = Files.newBufferedWriter(projectFilePath, StandardCharsets.UTF_8)) {
            writer.write(String.join("\n", lines));
        }
    }
}
