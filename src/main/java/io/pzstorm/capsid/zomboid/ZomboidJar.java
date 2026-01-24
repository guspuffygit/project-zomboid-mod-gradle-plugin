package io.pzstorm.capsid.zomboid;

import groovy.lang.Closure;
import io.pzstorm.capsid.mod.ModProperties;
import javax.annotation.Nullable;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.jvm.tasks.Jar;
import org.gradle.util.internal.GUtil;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class ZomboidJar extends Jar {

    protected ZomboidJar() {
        Project project = getProject();
        getArchiveFileName()
                .set(
                        project.provider(
                                () -> {
                                    String name = GUtil.elvis(getArchiveBaseName().getOrNull(), "");
                                    name += maybe(name, getArchiveAppendix().getOrNull());

                                    // omit version from name when no pz version property found
                                    String pzVersion =
                                            ModProperties.PZ_VERSION.findProperty(project);
                                    if (pzVersion != null) {
                                        name += maybe(name, pzVersion);
                                    }
                                    name += maybe(name, getArchiveClassifier().getOrNull());

                                    String extension = this.getArchiveExtension().getOrNull();
                                    return name + (GUtil.isTrue(extension) ? "." + extension : "");
                                }));
    }

    private static String maybe(@Nullable String prefix, @Nullable String value) {
        return GUtil.isTrue(value) ? GUtil.isTrue(prefix) ? "-".concat(value) : value : "";
    }

    @Override
    public Task configure(Closure closure) {
        Project project = getProject();

        dependsOn(project.getTasks().getByName(ZomboidTasks.ZOMBOID_VERSION.name));
        project.getTasks().getByName("jar").dependsOn(this);

        return super.configure(closure);
    }
}
