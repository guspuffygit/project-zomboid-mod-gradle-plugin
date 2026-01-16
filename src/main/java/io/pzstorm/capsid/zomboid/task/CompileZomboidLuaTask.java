package io.pzstorm.capsid.zomboid.task;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.ProjectProperty;
import io.pzstorm.capsid.setup.LocalProperties;
import io.pzstorm.capsid.util.UnixPath;
import io.pzstorm.capsid.zomboid.ZomboidTasks;
import java.nio.file.Paths;
import java.util.Objects;
import org.gradle.api.Project;

public abstract class CompileZomboidLuaTask extends ZomboidJavaExec implements CapsidTask {

    @Override
    public void configure(String group, String description, Project project) {
        UnixPath gameDir = Objects.requireNonNull(LocalProperties.GAME_DIR.findProperty(project));
        UnixPath zDocLuaDir = UnixPath.get(ProjectProperty.ZDOC_LUA_DIR.get(project));

        args(
                "compile",
                "-i",
                gameDir.toString(),
                "-o",
                Paths.get(zDocLuaDir.toString(), "media/lua/shared/Library").toString());
        shouldRunAfter(project.getTasks().getByName(ZomboidTasks.ANNOTATE_ZOMBOID_LUA.name));
        super.configure(group, description, project);
    }
}
