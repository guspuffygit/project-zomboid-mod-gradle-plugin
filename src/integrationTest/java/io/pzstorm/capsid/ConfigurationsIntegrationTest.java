package io.pzstorm.capsid;

import java.util.HashMap;
import java.util.Map;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ConfigurationsIntegrationTest extends PluginIntegrationTest {

    @Test
    void shouldRegisterConfigurations() {
        ConfigurationContainer configs1 = getProject(false).getConfigurations();
        Map<Configurations, Configuration> configData = new HashMap<>();

        for (Configurations value : Configurations.values()) {
            Configuration configuration = value.register(configs1);
            Assertions.assertTrue(configs1.contains(configuration));
            configData.put(value, configuration);
        }
        Project project = ProjectBuilder.builder().build();
        ConfigurationContainer configs2 = project.getConfigurations();

        // confirm that configurations are not registered in new project
        for (Configurations value : Configurations.values()) {
            Assertions.assertFalse(configs2.contains(configData.get(value)));
        }
    }
}
