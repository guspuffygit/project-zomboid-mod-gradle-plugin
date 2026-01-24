package io.pzstorm.capsid.mod.task;

import io.pzstorm.capsid.CapsidPlugin;
import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.ProjectProperty;
import io.pzstorm.capsid.mod.ModProperties;
import io.pzstorm.capsid.property.CapsidProperty;
import java.io.File;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

/** This task prints mod metadata information. */
public abstract class ShowModMetadataTask extends DefaultTask implements CapsidTask {

    @Input
    @Optional
    public abstract Property<String> getPzVersion();

    @Input
    @Optional
    public abstract Property<String> getModName();

    @Input
    @Optional
    public abstract Property<String> getModDescription();

    @Input
    @Optional
    public abstract Property<String> getModUrl();

    @Input
    @Optional
    public abstract Property<String> getModId();

    @Input
    @Optional
    public abstract Property<String> getModVersion();

    @Override
    public void configure(String group, String description, Project project) {
        CapsidTask.super.configure(group, description, project);
        File modInfoFile = ProjectProperty.MOD_INFO_FILE.get(project);
        onlyIf(t -> modInfoFile.exists());

        // 2. Bind the Project properties (ext) to the Task inputs (Configuration time)
        // We use project.provider() to lazily fetch values from 'ext'
        bindProperty(project, getPzVersion(), ModProperties.PZ_VERSION);
        bindProperty(project, getModName(), ModProperties.MOD_NAME);
        bindProperty(project, getModDescription(), ModProperties.MOD_DESCRIPTION);
        bindProperty(project, getModUrl(), ModProperties.MOD_URL);
        bindProperty(project, getModId(), ModProperties.MOD_ID);
        bindProperty(project, getModVersion(), ModProperties.MOD_VERSION);
    }

    private void bindProperty(
            Project project, Property<String> taskProp, CapsidProperty<?> capsidProp) {
        taskProp.set(
                project.provider(
                        () -> {
                            Object value =
                                    project.getExtensions()
                                                    .getExtraProperties()
                                                    .has(capsidProp.name)
                                            ? project.getExtensions()
                                                    .getExtraProperties()
                                                    .get(capsidProp.name)
                                            : null;
                            return (String) value;
                        }));
    }

    @TaskAction
    void execute() {
        Logger logger = CapsidPlugin.LOGGER;

        logger.lifecycle("This is a mod for Project Zomboid " + formatDisplay(getPzVersion()));
        logger.lifecycle("------------------------------------------------");
        logger.lifecycle("Name: " + formatDisplay(getModName()));
        logger.lifecycle("Description: " + formatDisplay(getModDescription()));
        logger.lifecycle("URL: " + formatDisplay(getModUrl()));
        logger.lifecycle("ID: " + formatDisplay(getModId()));
        logger.lifecycle("Version: " + formatDisplay(getModVersion()));
    }

    private String formatDisplay(Property<String> property) {
        String output = property.getOrElse("");
        return output.isEmpty() ? "<not specified>" : output;
    }
}
