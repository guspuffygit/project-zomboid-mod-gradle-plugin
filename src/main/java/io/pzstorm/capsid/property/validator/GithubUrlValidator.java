
package io.pzstorm.capsid.property.validator;

import io.pzstorm.capsid.property.InvalidCapsidPropertyException;
import java.net.URL;
import org.gradle.api.InvalidUserDataException;

/**
 * This class validates a {@code URL} as a valid Github page {@code URL}.
 *
 * @see <a href="https://github.com/">Github website</a>
 */
public class GithubUrlValidator implements PropertyValidator<URL> {

    /**
     * Returns {@code true} if given {@code URL} is a valid link to a Github page.
     *
     * @param property property to validate.
     */
    @Override
    public boolean isValid(URL property) {
        return property.getHost().equals("github.com");
    }

    @Override
    public URL validate(URL property) throws InvalidUserDataException {

        if (!isValid(property)) {
            String msg = "URL '%s' is not a valid Github URL";
            throw new InvalidCapsidPropertyException(String.format(msg, property));
        } else return property;
    }
}
