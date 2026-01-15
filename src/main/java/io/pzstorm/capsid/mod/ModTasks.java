/*
 * Storm Capsid - Project Zomboid mod development framework for Gradle.
 * Copyright (C) 2021 Matthew Cain
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
