package io.pzstorm.capsid.zomboid.task;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.ProjectProperty;
import io.pzstorm.capsid.setup.LocalProperties;
import io.pzstorm.capsid.util.UnixPath;
import java.nio.file.Paths;
import java.util.Objects;
import org.gradle.api.Project;

/** This task will annotate vanilla Lua with {@code EmmyLua}. */
public abstract class AnnotateZomboidLuaTask extends ZomboidJavaExec implements CapsidTask {

    @Override
    public void configure(String group, String description, Project project) {
        UnixPath gameDir = Objects.requireNonNull(LocalProperties.GAME_DIR.findProperty(project));
        UnixPath zDocLuaDir = UnixPath.get(ProjectProperty.ZDOC_LUA_DIR.get(project));

        args(
                "annotate",
                "-i",
                Paths.get(gameDir.toString(), "media/lua").toString(),
                "-o",
                Paths.get(zDocLuaDir.toString(), "media/lua").toString());
        super.configure(group, description, project);
    }
}
