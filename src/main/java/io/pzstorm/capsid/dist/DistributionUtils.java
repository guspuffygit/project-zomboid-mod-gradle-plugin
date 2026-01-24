package io.pzstorm.capsid.dist;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.gradle.api.GradleException;
import org.gradle.api.file.SourceDirectorySet;

public class DistributionUtils {

    /**
     * Returns a {@code Map} that contains paths relative to given module. Paths relate to existing
     * files from given {@code SourceDirectorySet}. The resulting map keys represent the source
     * directory, while the values represent paths relative to that source directory.
     *
     * @param module {@code File} representing module root directory.
     * @param srcDirSet {@code SourceDirectorySet} to extract paths from.
     * @return {@code Map} that contains paths relative to given module.
     */
    public static Map<Path, String> getPathsRelativeToModule(
            File module, SourceDirectorySet srcDirSet) {
        Map<Path, String> result = new HashMap<>();
        File moduleDir = module.getAbsoluteFile();
        if (!moduleDir.exists()) {
            throw new GradleException(
                    "Unable to find module directory for path '" + moduleDir.getPath() + '\'');
        }
        // collect all existing source directories for given directory set
        Set<File> srcDirs =
                srcDirSet.getSrcDirs().stream().filter(File::exists).collect(Collectors.toSet());

        for (File srcDir : srcDirs) {
            // existing file paths found in source directory
            Set<Path> paths;

            // recursively collect all file paths in source directory
            try (Stream<Path> stream = Files.walk(srcDir.toPath())) {
                paths = stream.filter(Files::isRegularFile).collect(Collectors.toSet());
            } catch (IOException e) {
                throw new GradleException("I/O error occurred while walking file tree", e);
            }
            Path srcDirPath = srcDir.toPath();
            for (Path path : paths) {
                // path relative to the source directory
                Path relativeToSrcDir = srcDirPath.relativize(path);

                // path to source root directory
                Path srdRootDirectory = moduleDir.toPath().relativize(srcDirPath);
                result.put(relativeToSrcDir, srdRootDirectory.toString());
            }
        }
        return result;
    }
}
