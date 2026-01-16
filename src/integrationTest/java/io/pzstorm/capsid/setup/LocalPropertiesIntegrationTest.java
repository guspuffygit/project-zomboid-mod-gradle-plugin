package io.pzstorm.capsid.setup;

import io.pzstorm.capsid.PluginIntegrationTest;
import io.pzstorm.capsid.property.CapsidProperty;
import io.pzstorm.capsid.util.Utils;
import java.io.IOException;
import java.util.Objects;
import org.gradle.api.Project;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LocalPropertiesIntegrationTest extends PluginIntegrationTest {

    @Test
    void shouldReturnFalseWhenLoadingNonExistingLocalProperties() {
        Project project = getProject(false);
        LocalProperties localProperties = LocalProperties.get();

        Assertions.assertTrue(localProperties.getFile(project).delete());
        Assertions.assertFalse(localProperties.load(project));
    }

    @Test
    void shouldWriteLocalPropertiesToFile() throws IOException {

        Project project = getProject(false);
        LocalProperties localProperties = LocalProperties.get();

        writeToProjectFile(
                "local.properties",
                new String[] {
                    String.format("gameDir=%s", getGameDirPath()),
                    String.format("ideaHome=%s", getIdeaHomePath())
                });
        // load properties for project before asserting
        localProperties.load(project);

        // write properties to file
        localProperties.writeToFile(project);

        StringBuilder sb = new StringBuilder();
        String[] expectedFileComments =
                new String[] {
                    "#This file contains local properties used to configure project build",
                    "#Note: paths need to be Unix-style where segments "
                            + "need to be separated with forward-slashes (/)",
                    "#this is for compatibility and stability purposes as backslashes don't play well."
                };
        sb.append(String.join("\n", expectedFileComments));
        for (CapsidProperty<?> property : localProperties.getProperties()) {
            String sProperty = Objects.requireNonNull(property.findProperty(project)).toString();

            sb.append("\n\n").append("#").append(property.comment).append('\n');
            sb.append(property.name).append('=').append(sProperty.replace('\\', '/'));
        }
        String expected = sb.toString();
        localProperties.writeToFile(project);
        String actual = Utils.readTextFromFile(localProperties.getFile(project));
        Assertions.assertEquals(expected, actual);
    }
}
