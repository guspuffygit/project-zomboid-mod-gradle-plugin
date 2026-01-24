package io.pzstorm.capsid.property;

import javax.annotation.Nullable;
import org.gradle.api.GradleException;

public class InvalidCapsidPropertyException extends GradleException {

    public InvalidCapsidPropertyException(String message) {
        super(message);
    }

    public InvalidCapsidPropertyException(String message, @Nullable Throwable cause) {
        super(message, cause);
    }
}
