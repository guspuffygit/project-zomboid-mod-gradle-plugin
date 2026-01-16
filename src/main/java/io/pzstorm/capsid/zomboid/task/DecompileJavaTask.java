package io.pzstorm.capsid.zomboid.task;

import com.google.common.collect.ImmutableMap;
import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.ProjectPropertiesSupplier;
import io.pzstorm.capsid.zomboid.ZomboidTasks;
import java.io.File;
import java.util.*;
import javax.inject.Inject;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.tasks.*;
import org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler;
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences;

/**
 * This class decompiles specified classes with FernFlower. Custom compiler parameters can be
 * specified in class constructor.
 */
public class DecompileJavaTask extends DefaultTask implements CapsidTask {

    private final ConfigurableFileCollection sourceFiles;
    private final DirectoryProperty destinationDir;
    private final MapProperty<String, Object> decompilerParameters;

    @Inject
    public DecompileJavaTask(
            ProjectPropertiesSupplier<?> source,
            ProjectPropertiesSupplier<File> destination,
            Map<String, Object> parameters) {

        ObjectFactory objects = getProject().getObjects();
        this.sourceFiles = objects.fileCollection();
        this.destinationDir = objects.directoryProperty();
        this.decompilerParameters = objects.mapProperty(String.class, Object.class);

        this.decompilerParameters.putAll(parameters);

        this.destinationDir.set(destination.getProjectProperty(getProject()));

        Object sourceObj = source.getProjectProperty(getProject());
        this.sourceFiles.from(sourceObj);
    }

    public DecompileJavaTask(
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
    }

    @Input
    public MapProperty<String, Object> getParameters() {
        return decompilerParameters;
    }

    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    @SkipWhenEmpty
    public ConfigurableFileCollection getInputFiles() {
        return sourceFiles;
    }

    @OutputDirectory
    public DirectoryProperty getOutputDirectory() {
        return destinationDir;
    }

    @Override
    public void configure(String group, String description, Project project) {
        CapsidTask.super.configure(group, description, project);
        dependsOn(project.getTasks().getByName(ZomboidTasks.ZOMBOID_CLASSES.name));
    }

    @TaskAction
    void execute() {
        List<String> args = new ArrayList<>();

        // Use .get() to access values from properties
        getParameters().get().forEach((k, v) -> args.add(k + "=" + v));

        for (File file : getInputFiles().getFiles()) {
            args.add(file.getAbsolutePath());
        }

        File destinationFile = getOutputDirectory().get().getAsFile();

        if (!destinationFile.exists()) {
            destinationFile.mkdirs();
        }

        args.add(destinationFile.getAbsolutePath());

        ConsoleDecompiler.main(args.toArray(new String[0]));
    }
}
