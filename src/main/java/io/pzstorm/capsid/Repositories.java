package io.pzstorm.capsid;

import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.ArtifactRepository;

public enum Repositories {

    /**
     * Represents Maven central repository.
     *
     * @see RepositoryHandler#mavenCentral()
     */
    MAVEN_CENTRAL(RepositoryHandler::mavenCentral),

    /**
     * Represents Maven local cache repository.
     *
     * @see RepositoryHandler#mavenLocal()
     */
    MAVEN_LOCAL(RepositoryHandler::mavenLocal);

    private final RepositorySupplier repository;

    Repositories(RepositorySupplier repository) {
        this.repository = repository;
    }

    /** Register this repository with the given {@link RepositoryHandler}. */
    ArtifactRepository register(RepositoryHandler handler) {
        return repository.get(handler);
    }
}
