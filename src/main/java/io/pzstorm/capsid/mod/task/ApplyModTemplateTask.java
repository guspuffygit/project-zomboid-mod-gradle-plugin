package io.pzstorm.capsid.mod.task;

import com.google.common.base.Splitter;
import io.pzstorm.capsid.CapsidPlugin;
import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.util.Utils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.tasks.Copy;

/** This task applies Project Zomboid mod template to root directory. */
public abstract class ApplyModTemplateTask extends Copy implements CapsidTask {

    @Override
    public void configure(String group, String description, Project project) {
        CapsidTask.super.configure(group, description, project);

        // template files will be copied to this directory
        File templateTempDir;
        try {
            /* extract the files from jar to a temporary directory,
             * then copy from there to project root directory
             */
            templateTempDir = Files.createTempDirectory("capsidModTemplate").toFile();
            List<String> templateFilePaths =
                    Splitter.on('\n')
                            .splitToList(
                                    Utils.readResourceAsTextFromStream(
                                            CapsidPlugin.class, "template/template.txt"));
            for (String templateFilePath : templateFilePaths) {
                // make sure directory structure exists before we write file from stream
                File targetFile = new File(templateTempDir, templateFilePath);
                File parentFile = targetFile.getParentFile();
                if (!parentFile.exists() && !parentFile.mkdirs()) {
                    throw new IOException(
                            "Unable to create directory structure for path '"
                                    + parentFile.getPath()
                                    + '\'');
                }
                Utils.readResourceAsFileFromStream(
                        CapsidPlugin.class, templateFilePath, targetFile);
            }
        } catch (IOException e) {
            throw new GradleException("I/O exception occurred while applying mod template", e);
        }
        from(new File(templateTempDir, "template"));
        into(project.getProjectDir());

        // overwrite if duplicate found in destination
        setDuplicatesStrategy(DuplicatesStrategy.INCLUDE);
    }
}
