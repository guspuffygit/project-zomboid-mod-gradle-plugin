
package io.pzstorm.capsid;

import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;

@Tag("unit")
public abstract class PluginUnitTest {

    protected static final File WORKSPACE = new File("build/tmp/unitTest");

    @BeforeAll
    static void createWorkspaceDirectory() throws IOException {

        if (!WORKSPACE.exists()) {
            Files.createDirectory(WORKSPACE.toPath());
        }
        RecursiveDeleteOption option = RecursiveDeleteOption.ALLOW_INSECURE;
        MoreFiles.deleteDirectoryContents(WORKSPACE.toPath(), option);
    }
}
