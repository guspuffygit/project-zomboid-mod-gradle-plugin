
package io.pzstorm.capsid.property.validator;

import io.pzstorm.capsid.property.InvalidCapsidPropertyException;
import org.gradle.api.InvalidUserDataException;
import org.jetbrains.annotations.Contract;

/**
 * This class validates properties according to implementation criteria.
 *
 * @param <T> type of property.
 */
public interface PropertyValidator<T> {

    /**
     * Returns {@code true} if the given property is valid.
     *
     * @param property property to validate.
     */
    @Contract(pure = true)
    boolean isValid(T property);

    /**
     * Validate the given property.
     *
     * @param property property to validate.
     * @return the given property.
     * @throws InvalidCapsidPropertyException if property is invalid.
     */
    @Contract(pure = true, value = "_ -> param1")
    T validate(T property) throws InvalidUserDataException;
}
