package io.pzstorm.capsid.setup.xml;

import io.pzstorm.capsid.CapsidGradleRunner;
import io.pzstorm.capsid.PluginFunctionalTest;
import io.pzstorm.capsid.mod.ModProperties;
import io.pzstorm.capsid.setup.SetupTasks;
import io.pzstorm.capsid.util.Utils;
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CreateDiscordIntegrationTaskFunctionalTest extends PluginFunctionalTest {

    CreateDiscordIntegrationTaskFunctionalTest() {
        super("testDiscordIntegration");
    }

    @Test
    void shouldCreateDiscordIntegrationConfigurationFile() throws IOException {

        CapsidGradleRunner runner =
                getRunner()
                        .withArguments(
                                String.format(
                                        "-P%s=%s",
                                        ModProperties.MOD_DESCRIPTION.name,
                                        "Testing Discord integration."),
                                SetupTasks.CREATE_DISCORD_INTEGRATION.name);
        assertTaskOutcomeSuccess(runner.build(), SetupTasks.CREATE_DISCORD_INTEGRATION.name);

        String expected = Utils.readResourceAsTextFromStream(getClass(), "discord.xml");
        String actual = Utils.readTextFromFile(new File(getProjectDir(), ".idea/discord.xml"));
        Assertions.assertEquals(expected, actual);
    }
}
