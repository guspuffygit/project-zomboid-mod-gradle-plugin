package io.pzstorm.capsid.property.validator;

import io.pzstorm.capsid.PluginUnitTest;
import io.pzstorm.capsid.util.UnixPath;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.gradle.api.InvalidUserDataException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DirectoryPathValidatorTest extends PluginUnitTest {

    @Test
    void shouldCorrectlyValidateDirectoryPath() throws IOException {

        File targetDir = new File(WORKSPACE, "targetDir");
        Files.createDirectory(targetDir.toPath());

        Assertions.assertTrue(targetDir.exists());
        Assertions.assertTrue(targetDir.isDirectory());

        DirectoryPathValidator validator = PropertyValidators.DIRECTORY_PATH_VALIDATOR;

        UnixPath unixTargetDir = UnixPath.get(targetDir);
        Assertions.assertTrue(validator.isValid(unixTargetDir));
        Assertions.assertDoesNotThrow(() -> validator.validate(unixTargetDir));

        // target does not exits
        Assertions.assertTrue(targetDir.delete());
        Assertions.assertFalse(validator.isValid(unixTargetDir));
        Assertions.assertThrows(
                InvalidUserDataException.class, () -> validator.validate(unixTargetDir));
        File targetFile = new File(WORKSPACE, "targetFile");
        Assertions.assertTrue(targetFile.createNewFile());
        targetFile.deleteOnExit();

        // target is not a directory
        UnixPath unixTargetFile = UnixPath.get(targetFile);
        Assertions.assertFalse(validator.isValid(unixTargetFile));
        Assertions.assertThrows(
                InvalidUserDataException.class, () -> validator.validate(unixTargetFile));
    }
}
