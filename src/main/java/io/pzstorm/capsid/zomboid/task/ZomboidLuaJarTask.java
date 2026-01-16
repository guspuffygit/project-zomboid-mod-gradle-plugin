package io.pzstorm.capsid.zomboid.task;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.zomboid.ZomboidJar;
import org.gradle.api.Project;

public abstract class ZomboidLuaJarTask extends ZomboidJar implements CapsidTask {

    @Override
    public void configure(String group, String description, Project project) {
        CapsidTask.super.configure(group, description, project);

        getArchiveBaseName().set("zdoc-lua");

        from(project.getExtensions().getExtraProperties().get("zDocLuaDir"));
        getDestinationDirectory().set(project.file("lib"));
    }
}
