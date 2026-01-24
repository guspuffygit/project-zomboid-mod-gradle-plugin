package io.pzstorm.capsid;

import org.gradle.api.Project;

/**
 * This class supplies a project property of given type.
 *
 * @param <T> type of property.
 */
public interface ProjectPropertiesSupplier<T> {

    /** Returns property with type for given {@code Project}. */
    T getProjectProperty(Project project);
}
