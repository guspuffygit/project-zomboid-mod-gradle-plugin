
package io.pzstorm.capsid;

import com.google.common.collect.ImmutableSet;
import io.pzstorm.capsid.mod.ModProperties;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import io.pzstorm.capsid.zomboid.ZomboidTasks;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.file.ConfigurableFileCollection;

public enum Dependencies {

    /** Libraries used by Project Zomboid during runtime. */
    ZOMBOID_LIBRARIES(
            "zomboidRuntimeOnly",
            project ->
                    ImmutableSet.of(
                            project.fileTree(
                                    CapsidPlugin.getGameDirProperty(project),
                                    t -> t.include("*.jar")))),

    /** Project Zomboid assets in {@code media} directory. */
    ZOMBOID_ASSETS(
            "zomboidImplementation",
            project ->
                    ImmutableSet.of(
                            project.files(
                                    new File(CapsidPlugin.getGameDirProperty(project), "media")))),

    /** Project Zomboid Java classes. */
    ZOMBOID_CLASSES(
            "zomboidImplementation",
            false,
            project -> {
                String modPzVersion = ModProperties.PZ_VERSION.findProperty(project);
                // Create a ConfigurableFileCollection so we can attach build dependencies
                ConfigurableFileCollection jarFile = project.files(
                        String.format(
                                "lib/zomboid%s.jar",
                                modPzVersion != null && !modPzVersion.isEmpty()
                                        ? "-" + modPzVersion
                                        : ""));
                // Explicitly declare that this file is built by the zomboidJar task
                jarFile.builtBy(ZomboidTasks.ZOMBOID_JAR.name);
                return ImmutableSet.of(jarFile);
            }),

    /**
     * Lua library compiler for Project Zomboid.
     *
     * @see <a href="https://search.maven.org/artifact/io.github.cocolabs/pz-zdoc">Artifact on
     *     Central Maven</a>
     */
    ZOMBOID_DOC(
            "zomboidDoc",
            project ->
                    ImmutableSet.of(
                            "io.github.cocolabs:pz-zdoc:3.+",
                            project.files(ProjectProperty.ZOMBOID_CLASSES_DIR.get(project)))),

    /** Lua library compiled with ZomboidDoc. */
    LUA_LIBRARY(
            "compileOnly",
            false,
            project -> {
                String modPzVersion = ModProperties.PZ_VERSION.findProperty(project);
                // Create a ConfigurableFileCollection so we can attach build dependencies
                ConfigurableFileCollection jarFile = project.files(
                        String.format(
                                "lib/zdoc-lua%s.jar",
                                modPzVersion != null && !modPzVersion.isEmpty()
                                        ? "-" + modPzVersion
                                        : ""));
                // Explicitly declare that this file is built by the zomboidLuaJar task
                jarFile.builtBy(ZomboidTasks.ZOMBOID_LUA_JAR.name);
                return ImmutableSet.of(jarFile);
            });

    final String configuration;
    final boolean availablePreEval;
    private final DependencyResolver resolver;

    Dependencies(String configuration, boolean availablePreEval, DependencyResolver resolver) {
        this.configuration = configuration;
        this.availablePreEval = availablePreEval;
        this.resolver = resolver;
    }

    Dependencies(String configuration, DependencyResolver resolver) {
        this(configuration, true, resolver);
    }

    /**
     * Register dependencies for {@code Project} with the given {@code DependencyHandler}.
     *
     * @param project {@code Project} to register the dependencies for.
     * @param dependencies handler used to register dependencies.
     * @return {@code Set} of registered dependencies empty {@code Set} if none registered.
     */
    Set<Dependency> register(Project project, DependencyHandler dependencies) {
        Set<Dependency> result = new HashSet<>();
        Set<Object> dependencyNotations = resolver.resolveDependencies(project);
        for (Object notation : dependencyNotations) {
            result.add(dependencies.add(configuration, notation));
        }
        return result;
    }
}
