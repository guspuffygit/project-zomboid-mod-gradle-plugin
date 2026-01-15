
package io.pzstorm.capsid.util;

import io.pzstorm.capsid.PluginIntegrationTest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UtilsIntegrationTest extends PluginIntegrationTest {

    @Test
    void shouldGetFileFromResources() throws FileNotFoundException {

        File expected = new File("build/resources/integrationTest/dummy.zip");
        Assertions.assertEquals(
                expected.getAbsoluteFile(), Utils.getFileFromResources("dummy.zip"));
    }

    @Test
    void shouldUnzipArchive() throws IOException {

        File projectDir = getProject(false).getProjectDir();
        File archive = Utils.getFileFromResources("dummy.zip");

        File[] expected =
                new File[] {new File(projectDir, "dummy.txt"), new File(projectDir, "dummy.png")};
        for (File expectedFile : expected) {
            Assertions.assertFalse(expectedFile.exists());
        }
        Utils.unzipArchive(archive, projectDir);

        for (File expectedFile : expected) {
            Assertions.assertTrue(expectedFile.exists());
        }
    }
}
