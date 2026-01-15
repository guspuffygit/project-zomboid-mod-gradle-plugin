
package io.pzstorm.capsid.zomboid.task;

import io.pzstorm.capsid.PluginIntegrationTest;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ZomboidScriptIntegrationTest extends PluginIntegrationTest {

    @Test
    void shouldCreateZomboidClassesAndSourcesDirectoryProperties() {
        ExtraPropertiesExtension ext = getProject(true).getExtensions().getExtraProperties();

        Assertions.assertTrue(ext.has("zomboidClassesDir"));
        Assertions.assertTrue(ext.has("zomboidSourcesDir"));
    }

    @Test
    void shouldCreateProjectZomboidConfigurations() {
        ConfigurationContainer configurations = getProject(true).getConfigurations();

        Assertions.assertDoesNotThrow(() -> configurations.getByName("zomboidRuntimeOnly"));
        Assertions.assertDoesNotThrow(() -> configurations.getByName("zomboidImplementation"));
    }
}
