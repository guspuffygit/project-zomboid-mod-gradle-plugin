
package io.pzstorm.capsid.mod.task;

import com.google.common.base.Splitter;
import io.pzstorm.capsid.CapsidPlugin;
import io.pzstorm.capsid.PluginFunctionalTest;
import io.pzstorm.capsid.mod.ModTasks;
import io.pzstorm.capsid.util.Utils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.gradle.testkit.runner.BuildResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ApplyModTemplateTaskFunctionalTest extends PluginFunctionalTest {

    @Test
    void shouldApplyModTemplateToProjectRootDirectory() throws IOException {

        List<File> expectedFiles = new ArrayList<>();
        Splitter.on('\n')
                .splitToList(
                        Utils.readResourceAsTextFromStream(
                                CapsidPlugin.class, "template/template.txt"))
                .forEach(
                        path -> {
                            String relPath =
                                    Paths.get("template").relativize(Paths.get(path)).toString();
                            expectedFiles.add(new File(getProjectDir(), relPath));
                        });
        for (File expectedFile : expectedFiles) {
            Assertions.assertFalse(expectedFile.exists());
        }
        BuildResult result = getRunner().withArguments(ModTasks.APPLY_MOD_TEMPLATE.name).build();
        assertTaskOutcomeSuccess(result, ModTasks.APPLY_MOD_TEMPLATE.name);

        for (File expectedFile : expectedFiles) {
            Assertions.assertTrue(expectedFile.exists());
        }
    }
}
