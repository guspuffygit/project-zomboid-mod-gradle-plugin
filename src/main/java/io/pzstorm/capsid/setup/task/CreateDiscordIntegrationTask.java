package io.pzstorm.capsid.setup.task;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.setup.xml.DiscordIntegration;
import java.io.IOException;
import javax.xml.transform.TransformerException;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

/**
 * This task will create an {@code XML} file that shows IDEA project in Discord via rich presence.
 *
 * @see DiscordIntegration
 */
public class CreateDiscordIntegrationTask extends DefaultTask implements CapsidTask {

    @TaskAction
    void execute() throws IOException, TransformerException {
        DiscordIntegration.INTEGRATION.configure(getProject()).writeToFile();
    }
}
