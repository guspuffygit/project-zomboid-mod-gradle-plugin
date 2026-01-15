
package io.pzstorm.capsid.property;

import io.pzstorm.capsid.PluginIntegrationTest;
import io.pzstorm.capsid.property.validator.PropertyValidators;
import io.pzstorm.capsid.util.UnixPath;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CapsidPropertyIntegrationTest extends PluginIntegrationTest {

    @Test
    void shouldCorrectlyConvertLocalPropertyToUnixPath() throws IOException {

        Project project = getProject(false);
        File targetDir = new File(project.getProjectDir(), "targetDir");
        Files.createDirectory(targetDir.toPath());

        Assertions.assertTrue(targetDir.exists());
        CapsidProperty<UnixPath> testProperty =
                new CapsidProperty.Builder<>("testProperty", UnixPath.class)
                        .withValidator(PropertyValidators.DIRECTORY_PATH_VALIDATOR)
                        .build();

        ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();
        UnixPath expectedPath = UnixPath.get(targetDir);

        // test converting from string to path
        ext.set("testProperty", expectedPath.toString());
        Assertions.assertEquals(expectedPath, testProperty.findProperty(project));

        // test not converting and just validating
        ext.set("testProperty", expectedPath);
        Assertions.assertEquals(expectedPath, testProperty.findProperty(project));

        // test unsupported type throwing exception
        ext.set("testProperty", new Object());
        Assertions.assertThrows(
                InvalidCapsidPropertyException.class, () -> testProperty.findProperty(project));
    }
}
