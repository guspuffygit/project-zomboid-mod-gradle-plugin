
package io.pzstorm.capsid;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.jvm.toolchain.JavaToolchainSpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CapsidPluginIntegrationTest extends PluginIntegrationTest {

    @Test
    void shouldApplyAllCorePlugins() {
        PluginContainer plugins = getProject(true).getPlugins();
        for (CorePlugin plugin : CorePlugin.values()) {
            Assertions.assertTrue(plugins.hasPlugin(plugin.getID()));
        }
    }

    @Test
    void shouldRegisterAllRepositories() {
        RepositoryHandler repositories = getProject(true).getRepositories();
        Assertions.assertEquals(2, repositories.size());
        Assertions.assertNotNull(repositories.findByName("MavenRepo"));
    }

    @Test
    void shouldConfigureJavaToolchainLanguageLevel() {
        JavaPluginExtension java =
                Objects.requireNonNull(
                        getProject(true).getExtensions().getByType(JavaPluginExtension.class));
        JavaToolchainSpec toolchain = java.getToolchain();
        Assertions.assertEquals(8, toolchain.getLanguageVersion().get().asInt());
    }

    @Test
    void shouldCreateCustomSourceSetsWithSourceDirs() {
        Project project = getProject(true);
        JavaPluginExtension java = project.getExtensions().getByType(JavaPluginExtension.class);
        SourceSetContainer sourceSets = java.getSourceSets();

        SourceSet mediaSourceSet = sourceSets.getByName("media");
        Set<File> sourceDirs = mediaSourceSet.getJava().getSrcDirs();

        Path expectedPath = project.getProjectDir().toPath().resolve("media/lua");
        Assertions.assertTrue(sourceDirs.stream().anyMatch(d -> d.toPath().equals(expectedPath)));
    }
}
