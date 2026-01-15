
package io.pzstorm.capsid.setup.task;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.setup.xml.ModSearchScope;
import java.io.IOException;
import javax.xml.transform.TransformerException;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

/**
 * This task will create useful IDEA search scopes.
 *
 * @see ModSearchScope
 */
public class CreateSearchScopesTask extends DefaultTask implements CapsidTask {

    @TaskAction
    void execute() throws IOException, TransformerException {

        Project project = getProject();

        ModSearchScope.MOD_LUA.configure(project).writeToFile();
        ModSearchScope.MOD_MEDIA.configure(project).writeToFile();
        ModSearchScope.PZ_JAVA.configure(project).writeToFile();
        ModSearchScope.PZ_LUA.configure(project).writeToFile();
    }
}
