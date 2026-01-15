
package io.pzstorm.capsid;

import com.google.errorprone.annotations.Immutable;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.ArtifactRepository;

/** This interface resolves {@link ArtifactRepository} instances on demand. */
@Immutable
interface RepositorySupplier {

    /**
     * Resolve a {@link ArtifactRepository} instance from given {@link RepositoryHandler}.
     *
     * @param handler repository handler used to resolve the repository.
     */
    ArtifactRepository get(RepositoryHandler handler);
}
