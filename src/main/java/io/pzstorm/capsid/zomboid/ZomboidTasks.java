package io.pzstorm.capsid.zomboid;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.zomboid.task.*;
import org.gradle.api.Project;

public enum ZomboidTasks {
    ZOMBOID_CLASSES(
            ZomboidClassesTask.class, "zomboidClasses", "Assembles Project Zomboid classes."),
    DECOMPILE_ZOMBOID(
            DecompileZomboidTask.class, "decompileZomboid", "Decompile Project Zomboid classes."),
    DECOMPILE_ZOMBOID_LIBRARY(
            DecompileZomboidLibrariesTask.class,
            "decompileZomboidLibraries",
            "Decompile Project Zomboid library classes"),
    ZOMBOID_JAR(
            ZomboidJarTask.class, "zomboidJar", "Assembles a jar archive containing game classes."),
    ZOMBOID_SOURCES_JAR(
            ZomboidSourcesJarTask.class,
            "zomboidSourcesJar",
            "Assembles a jar containing decompiled game sources."),
    ZOMBOID_LUA_JAR(
            ZomboidLuaJarTask.class,
            "zomboidLuaJar",
            "Assembles a jar containing compiled Lua classes"),
    ZOMBOID_VERSION(
            ZomboidVersionTask.class,
            "zomboidVersion",
            "Save and print Project Zomboid game version."),
    ANNOTATE_ZOMBOID_LUA(
            AnnotateZomboidLuaTask.class,
            "annotateZomboidLua",
            "Annotate vanilla Lua with EmmyLua."),
    COMPILE_ZOMBOID_LUA(
            CompileZomboidLuaTask.class,
            "compileZomboidLua",
            "Compile Lua library from modding API."),
    UPDATE_ZOMBOID_LUA(
            UpdateZomboidLuaTask.class,
            "updateZomboidLua",
            "Run ZomboidDoc to update compiled Lua library.");
    public final String name, description;
    private final Class<? extends CapsidTask> type;

    ZomboidTasks(Class<? extends CapsidTask> type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    /**
     * Configure and register this task for the given {@code Project}.
     *
     * @param project {@code Project} register this task.
     */
    public void register(Project project) {
        project.getTasks().register(name, type, t -> t.configure("zomboid", description, project));
    }
}
