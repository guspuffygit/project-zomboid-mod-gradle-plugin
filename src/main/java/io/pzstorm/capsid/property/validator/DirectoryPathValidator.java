
package io.pzstorm.capsid.property.validator;

import io.pzstorm.capsid.util.UnixPath;
import java.io.File;
import org.gradle.api.InvalidUserDataException;

/** This class validates a directory {@link UnixPath}. */
public class DirectoryPathValidator implements PropertyValidator<UnixPath> {

    // make constructor available only from package
    DirectoryPathValidator() {}

    /**
     * Returns {@code true} if given path represents an existing directory.
     *
     * @param property property to validate.
     */
    @Override
    public boolean isValid(UnixPath property) {
        File file = property.convert().toFile();
        return file.exists() && file.isDirectory();
    }

    @Override
    public UnixPath validate(UnixPath property) {
        if (!isValid(property)) {
            String message = "Invalid directory path '%s'";
            throw new InvalidUserDataException(String.format(message, property));
        }
        return property;
    }
}
