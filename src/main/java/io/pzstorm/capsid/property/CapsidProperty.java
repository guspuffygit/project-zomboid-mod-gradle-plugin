
package io.pzstorm.capsid.property;

import com.google.common.base.Strings;
import io.pzstorm.capsid.property.validator.PropertyValidator;
import io.pzstorm.capsid.util.SemanticVersion;
import io.pzstorm.capsid.util.UnixPath;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents a property loaded from properties file.
 *
 * @param <T> type of property.
 */
public class CapsidProperty<T> {

    public final String name;
    public final Class<T> type;
    public final String comment;
    public final boolean required;

    private final String env;
    private final @Nullable T defaultValue;
    private final @Nullable PropertyValidator<T> validator;

    private CapsidProperty(Builder<T> builder) {
        this.name = builder.name;
        this.env = builder.env;
        this.comment = builder.comment;
        this.type = builder.type;
        this.defaultValue = builder.defaultValue;
        this.required = builder.required;
        this.validator = builder.validator;
    }

    /**
     * Returns the value assigned to key matching this property. Try to find the property value in
     * the following locations:
     *
     * <ul>
     *   <li>Project properties.
     *   <li>System properties.
     *   <li>Environment variables.
     * </ul>
     *
     * @return value matching this property or default value.
     * @throws InvalidCapsidPropertyException when a property was not found or was found but is of
     *     unsupported type.
     */
    public @Nullable T findProperty(Project project) {
        ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();
        if (ext.has(name)) {
            Object foundProperty = Objects.requireNonNull(ext.get(name));
            if (foundProperty instanceof String) {
                return convertAndValidateProperty((String) foundProperty);
            } else if (type.isInstance(foundProperty)) {
                T result = type.cast(foundProperty);
                return validator != null ? validator.validate(result) : result;
            } else
                throw new InvalidCapsidPropertyException(
                        String.format(
                                "Found capsid property " + "is of unsupported type '%s'",
                                foundProperty.getClass().getName()));
        }
        // try to find a matching system property first
        String sysProperty = System.getProperty(name);
        if (sysProperty == null) {
            // when env parameter is not defined search for env variable with property name
            String envVar = System.getenv(env != null && !env.isEmpty() ? env : name);
            if (envVar != null) {
                return convertAndValidateProperty(envVar);
            } else if (required && defaultValue == null) {
                throw new InvalidCapsidPropertyException("Unable to find capsid property " + name);
            } else return defaultValue;
        } else return convertAndValidateProperty(sysProperty);
    }

    /**
     * Convert and validate the given property to {@code Class} {@link #type}.
     *
     * @param property property to convert and validate.
     * @return converted property or {@code null} if property is {@code null} or empty.
     * @throws InvalidCapsidPropertyException if property is of unsupported type.
     */
    @SuppressWarnings("unchecked")
    @Contract("null -> null")
    private @Nullable T convertAndValidateProperty(String property) {
        if (Strings.isNullOrEmpty(property)) {
            return null;
        }
        if (type.equals(String.class)) {
            T result = (T) property;
            return validator != null ? validator.validate(result) : result;
        }
        if (type.equals(Boolean.class)) {
            T result = (T) Boolean.valueOf(property.equals("true"));
            return validator != null ? validator.validate(result) : result;
        }
        if (type.equals(SemanticVersion.class)) {
            T result = (T) new SemanticVersion(property);
            return validator != null ? validator.validate(result) : result;
        }
        if (type.equals(Path.class)) {
            T result = (T) Paths.get(property);
            return validator != null ? validator.validate(result) : result;
        }
        if (type.equals(UnixPath.class)) {
            T result = (T) UnixPath.get(property);
            return validator != null ? validator.validate(result) : result;
        }
        if (type.equals(URL.class)) {
            try {
                return (T) new URL(property);
            } catch (MalformedURLException e) {
                throw new InvalidCapsidPropertyException("Malformed URL property", e);
            }
        }
        throw new InvalidCapsidPropertyException(
                "Unsupported capsid property type " + type.getName());
    }

    // @formatter:off
    public static class Builder<T> {

        private final String name;
        private final Class<T> type;

        private String env = "";
        private String comment = "";
        private boolean required = true;

        private @Nullable T defaultValue = null;
        private @Nullable PropertyValidator<T> validator = null;

        /**
         * Start building {@link CapsidProperty} instance.
         *
         * @param name name of the property to build.
         * @param type class type of this property.
         */
        public Builder(String name, Class<T> type) {
            this.name = name;
            this.type = type;
        }

        /** Associate property with given environment variable. */
        @Contract("_ -> this")
        public Builder<T> withEnvironmentVar(String env) {
            this.env = env;
            return this;
        }

        /**
         * Set default value to use when property is not found.
         *
         * @see #isRequired(boolean)
         */
        @Contract("_ -> this")
        public Builder<T> withDefaultValue(@Nullable T defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        /** Set property comment to be written when saved to {@code Properties} file. */
        @Contract("_ -> this")
        public Builder<T> withComment(String comment) {
            this.comment = comment;
            return this;
        }

        /**
         * Marks the property as <i>required</i> or not. Finding the property with {@link
         * #findProperty(Project)} will produce an exception if a required property cannot be found
         * and no default value is designated.
         */
        @Contract("_ -> this")
        public Builder<T> isRequired(boolean required) {
            this.required = required;
            return this;
        }

        /** Designate a property validator used to assert if property is valid or not. */
        @Contract("_ -> this")
        public Builder<T> withValidator(PropertyValidator<T> validator) {
            this.validator = validator;
            return this;
        }

        /** Returns a new instance of {@link CapsidProperty} with held configuration. */
        @Contract(" -> new")
        public CapsidProperty<T> build() {
            return new CapsidProperty<>(this);
        }
    } // @formatter:on
}
