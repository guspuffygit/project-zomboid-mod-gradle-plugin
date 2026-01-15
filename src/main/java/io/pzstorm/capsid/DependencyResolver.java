
package io.pzstorm.capsid;

import com.google.errorprone.annotations.Immutable;
import java.util.Set;
import org.gradle.api.Project;

@Immutable
interface DependencyResolver {

    /**
     * Resolve dependency notations, files or paths for given project.
     *
     * @param project {@code Project} resolving dependencies.
     * @return resolved dependency objects.
     */
    Set<Object> resolveDependencies(Project project);
}
