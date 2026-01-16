package io.pzstorm.capsid.mod;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.mod.task.*;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;

public enum ModTasks {
    CREATE_MOD_STRUCTURE(
            CreateModStructureTask.class,
            "createModStructure",
            "Create default mod directory structure."),
    SAVE_MOD_METADATA(SaveModMetadataTask.class, "saveModMetadata", "Save mod metadata to file."),
    LOAD_MOD_METADATA(
            LoadModMetadataTask.class, "loadModMetadata", "Load mod metadata information."),
    INIT_MOD_METADATA(
            InitModMetadataTask.class, "initModMetadata", "Initialize mod metadata information."),
    SHOW_MOD_METADATA(
            ShowModMetadataTask.class, "showModMetadata", "Print mod metadata information."),
    APPLY_MOD_TEMPLATE(
            ApplyModTemplateTask.class, "applyModTemplate", "Apply Project Zomboid mod template.");
    public final String name, description;
    private final Class<? extends CapsidTask> type;

    ModTasks(Class<? extends CapsidTask> type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public void register(Project project) {
        TaskContainer tasks = project.getTasks();

        TaskProvider<? extends CapsidTask> provider =
                tasks.register(name, type, t -> t.configure("mod", description, project));

        // Force loading instead of lazy loading
        provider.get();
    }
}
