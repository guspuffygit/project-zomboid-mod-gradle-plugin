package io.pzstorm.capsid.zomboid.task;

import com.google.common.collect.ImmutableMap;
import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.ProjectPropertiesSupplier;
import io.pzstorm.capsid.zomboid.ZomboidTasks;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import javax.inject.Inject;
import org.gradle.api.DefaultTask;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;
import org.gradle.api.tasks.*;
import org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler;
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences;

/**
 * This class decompiles specified classes with FernFlower. Custom compiler parameters can be
 * specified in class constructor.
 */
public class DecompileJavaTask extends DefaultTask implements CapsidTask {

    private final ProjectPropertiesSupplier<?> source;
    private final ProjectPropertiesSupplier<File> destination;
    private final Map<String, Object> parameters;

    // @formatter:off
    @Inject
    DecompileJavaTask(
            ProjectPropertiesSupplier<?> source,
            ProjectPropertiesSupplier<File> destination,
            Map<String, Object> parameters) {
        this.source = source;
        this.destination = destination;
        this.parameters = parameters;
    }

    DecompileJavaTask(
            ProjectPropertiesSupplier<?> source, ProjectPropertiesSupplier<File> destination) {
        // default parameters used by IDEA compiler
        this(
                source,
                destination,
                ImmutableMap.<String, Object>builder()
                        .put(IFernflowerPreferences.HIDE_DEFAULT_CONSTRUCTOR, "0")
                        .put(IFernflowerPreferences.DECOMPILE_GENERIC_SIGNATURES, "1")
                        .put(IFernflowerPreferences.REMOVE_SYNTHETIC, "1")
                        .put(IFernflowerPreferences.REMOVE_BRIDGE, "1")
                        .put(IFernflowerPreferences.LITERALS_AS_IS, "1")
                        .put(IFernflowerPreferences.NEW_LINE_SEPARATOR, "1")
                        .put(IFernflowerPreferences.MAX_PROCESSING_METHOD, "60")
                        .build());
    } // @formatter:on

    @Input
    public Map<String, Object> getParameters() {
        return parameters;
    }

    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    @SkipWhenEmpty
    public List<File> getInputFiles() {
        List<File> files = new ArrayList<>();
        for (Path path : getSourcePaths(getProject())) {
            files.add(path.toFile());
        }
        return files;
    }

    @OutputDirectory
    public File getOutputDirectory() {
        return destination.getProjectProperty(getProject());
    }

    @Override
    public void configure(String group, String description, Project project) {
        CapsidTask.super.configure(group, description, project);

        dependsOn(project.getTasks().getByName(ZomboidTasks.ZOMBOID_CLASSES.name));
    }

    @TaskAction
    void execute() {
        List<String> args = new ArrayList<>();
        getParameters().forEach((k, v) -> args.add(k + '=' + v));

        for (File file : getInputFiles()) {
            args.add(file.getAbsolutePath());
        }

        File destinationFile = getOutputDirectory();

        if (!destinationFile.exists()) {
            destinationFile.mkdirs();
        }

        args.add(destinationFile.getAbsolutePath());

        ConsoleDecompiler.main(args.toArray(new String[0]));
    }

    /**
     * Returns list of source paths to decompile from.
     *
     * @param project {@code Project} used to resolve the project property.
     */
    @Internal
    List<Path> getSourcePaths(Project project) {
        List<Path> result = new ArrayList<>();
        Object oSource = source.getProjectProperty(project);
        if (oSource instanceof Iterable) {
            //noinspection rawtypes
            for (Object object : ((Iterable) oSource)) {
                result.add(getSourcePathFromObject(object));
            }
        } else result.add(getSourcePathFromObject(oSource));
        return Collections.unmodifiableList(result);
    }

    /**
     * Resolve a {@code Path} from given {@code Object}.
     *
     * @throws InvalidUserDataException if objects is an unsupported class.
     */
    @Internal
    Path getSourcePathFromObject(Object object) {
        if (object instanceof File) {
            return ((File) object).toPath();
        } else if (object instanceof Path) {
            return (Path) object;
        } else if (object instanceof String) {
            return Paths.get((String) object);
        } else
            throw new InvalidUserDataException(
                    "Unsupported source path type " + object.getClass().getName());
    }
}
