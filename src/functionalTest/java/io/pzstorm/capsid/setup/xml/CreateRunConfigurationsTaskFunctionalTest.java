package io.pzstorm.capsid.setup.xml;

import com.google.common.collect.ImmutableMap;
import io.pzstorm.capsid.CapsidGradleRunner;
import io.pzstorm.capsid.PluginFunctionalTest;
import io.pzstorm.capsid.setup.SetupTasks;
import io.pzstorm.capsid.util.Utils;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.gradle.internal.os.OperatingSystem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CreateRunConfigurationsTaskFunctionalTest extends PluginFunctionalTest {

    private static final ImmutableMap<XMLDocument, String> RUN_CONFIGS =
            ImmutableMap.<XMLDocument, String>builder()
                    .put(LaunchRunConfig.RUN_ZOMBOID, "Run_Zomboid")
                    .put(LaunchRunConfig.RUN_ZOMBOID_LOCAL, "Run_Zomboid_local")
                    .put(LaunchRunConfig.DEBUG_ZOMBOID, "Debug_Zomboid")
                    .put(LaunchRunConfig.DEBUG_ZOMBOID_LOCAL, "Debug_Zomboid_local")
                    .put(GradleRunConfig.INITIALIZE_MOD, "initializeMod")
                    .put(GradleRunConfig.SETUP_WORKSPACE, "setupWorkspace")
                    .build();

    CreateRunConfigurationsTaskFunctionalTest() {
        super("testLaunchRunConfigs");
    }

    @Test
    void shouldWriteToFileLaunchRunConfigurationsFromTask() throws IOException {

        CapsidGradleRunner runner =
                getRunner()
                        .withArguments(
                                "-x" + SetupTasks.SET_GAME_DIRECTORY.name,
                                SetupTasks.CREATE_RUN_CONFIGS.name);
        assertTaskOutcomeSuccess(runner.build(), SetupTasks.CREATE_RUN_CONFIGS.name);

        File runConfigurations = new File(getProjectDir(), ".idea/runConfigurations");
        for (Map.Entry<XMLDocument, String> entry : RUN_CONFIGS.entrySet()) {
            String filename = entry.getValue();
            File runConfig = new File(runConfigurations, filename + ".xml");
            Assertions.assertTrue(runConfig.exists());

            String resourceFilename = filename;
            boolean isLinux = OperatingSystem.current() == OperatingSystem.LINUX;
            if (isLinux && entry.getKey() instanceof LaunchRunConfig) {
                resourceFilename += "_linux";
            }
            String expected =
                    Utils.readResourceAsTextFromStream(getClass(), resourceFilename + ".xml");
            Assertions.assertEquals(expected, Utils.readTextFromFile(runConfig));
        }
    }
}
