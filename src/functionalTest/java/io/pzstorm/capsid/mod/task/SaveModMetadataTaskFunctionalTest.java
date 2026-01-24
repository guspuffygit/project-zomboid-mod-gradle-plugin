package io.pzstorm.capsid.mod.task;

import io.pzstorm.capsid.PluginFunctionalTest;
import io.pzstorm.capsid.mod.ModTasks;
import io.pzstorm.capsid.zomboid.ZomboidTasks;
import org.gradle.testkit.runner.BuildResult;
import org.junit.jupiter.api.Test;

class SaveModMetadataTaskFunctionalTest extends PluginFunctionalTest {

    @Test
    void shouldSuccessfullyExecuteSaveModInfoTask() {
        BuildResult result =
                getRunner()
                        .withArguments(
                                ModTasks.SAVE_MOD_METADATA.name,
                                "-x" + ZomboidTasks.ZOMBOID_VERSION.name)
                        .build();
        assertTaskOutcomeSuccess(result, ModTasks.SAVE_MOD_METADATA.name);
    }
}
