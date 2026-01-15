
package io.pzstorm.capsid;

import java.util.HashMap;
import java.util.Map;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RepositoriesIntegrationTest extends PluginIntegrationTest {

    @Test
    void shouldRegisterRepositories() {
        RepositoryHandler handler1 = getProject(false).getRepositories();
        Map<Repositories, ArtifactRepository> repositoryData = new HashMap<>();

        for (Repositories value : Repositories.values()) {
            ArtifactRepository repository = value.register(handler1);
            Assertions.assertTrue(handler1.contains(repository));
            repositoryData.put(value, repository);
        }
        Project project = ProjectBuilder.builder().build();
        RepositoryHandler handler2 = project.getRepositories();

        // confirm that repositories are not registered in new project
        for (Repositories value : Repositories.values()) {
            Assertions.assertFalse(handler2.contains(repositoryData.get(value)));
        }
    }
}
