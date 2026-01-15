
package io.pzstorm.capsid.zomboid;

import io.pzstorm.capsid.Configurations;
import io.pzstorm.capsid.util.SemanticVersion;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.gradle.api.GradleException;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;

/** This class contains helper methods used by {@link ZomboidTasks}. */
public class ZomboidUtils {

    /**
     * Returns the ZomboidDoc dependency version used by given project.
     *
     * @throws GradleException if unable to find dependency or dependency has unexpected name.
     * @throws InvalidUserDataException if constructed semantic version is malformed.
     */
    public static SemanticVersion getZomboidDocVersion(Project project) {
        // find ZomboidDoc dependency file from configuration
        File dependency =
                Configurations.ZOMBOID_DOC.resolve(project).getFiles().stream()
                        .filter(f -> f.getName().startsWith("pz-zdoc"))
                        .findFirst()
                        .orElseThrow(
                                () -> new GradleException("Unable to find ZomboidDoc dependency"));

        // get and validate dependency name
        String dependencyName = dependency.getName();

        String pattern = "pz-zdoc-(\\d+\\.\\d+\\.\\d+(-.*)?)\\.jar";
        Matcher matcher = Pattern.compile(pattern).matcher(dependencyName);
        if (!matcher.find()) {
            throw new GradleException("Unexpected ZomboidDoc dependency name: " + dependencyName);
        }
        return new SemanticVersion(matcher.group(1));
    }
}
