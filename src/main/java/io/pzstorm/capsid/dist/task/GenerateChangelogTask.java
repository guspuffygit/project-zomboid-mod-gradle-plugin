package io.pzstorm.capsid.dist.task;

import com.google.common.base.Strings;
import io.pzstorm.capsid.CapsidPlugin;
import io.pzstorm.capsid.CapsidPluginExtension;
import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.dist.GenerateChangelogOptions;
import io.pzstorm.capsid.util.Utils;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.api.tasks.Exec;

/** This task generates a changelog using {@code github-changelog-generator}. */
public abstract class GenerateChangelogTask extends Exec implements CapsidTask {

    private static final String TOKEN_ENV_VAR_NAME = "CHANGELOG_GITHUB_TOKEN";
    private static final String TOKEN_PROPERTY_NAME = "gcl.token";

    /**
     * Create {@code Gemfile} needed to generate changelog with Ruby if it does not exist.
     *
     * @param project {@code Project} to create the file for.
     * @throws GradleException if an I/O exception occurred while creating {@code Gemfile}.
     */
    private static void createGemfile(Project project) {
        File gemFile = new File(project.getProjectDir(), "Gemfile");
        if (gemFile.exists()) {
            return;
        }
        try {
            if (!gemFile.createNewFile()) {
                throw new GradleException("Unable to create Gemfile in root directory");
            }
            try (Writer writer =
                    Files.newBufferedWriter(gemFile.toPath(), StandardCharsets.UTF_8)) {
                writer.write(Utils.readResourceAsTextFromStream(CapsidPlugin.class, "Gemfile"));
            }
        } catch (IOException e) {
            throw new GradleException(
                    "I/O error occurred while creating Gemfile in root directory", e);
        }
    }

    @Override
    public void configure(String group, String description, Project project) {
        CapsidTask.super.configure(group, description, project);

        ExtensionContainer extensions = project.getExtensions();
        ExtraPropertiesExtension ext = extensions.getExtraProperties();
        CapsidPluginExtension capsidExt = extensions.getByType(CapsidPluginExtension.class);

        String tRepoOwner = capsidExt.getProjectRepositoryOwner();
        if (Strings.isNullOrEmpty(tRepoOwner) && ext.has("repo.owner")) {
            tRepoOwner = (String) ext.get("repo.owner");
        }
        String tRepoName = capsidExt.getProjectRepositoryName();
        if (Strings.isNullOrEmpty(tRepoName) && ext.has("repo.name")) {
            tRepoName = (String) ext.get("repo.name");
        }
        // assign data to final variables so they can be used in lambdas
        final String repoOwner = tRepoOwner, repoName = tRepoName;

        // cannot generate changelog without knowing where to look
        boolean hasDefinedRepo =
                !Strings.isNullOrEmpty(repoOwner) && !Strings.isNullOrEmpty(repoName);
        onlyIf(it -> hasDefinedRepo);

        Map<GenerateChangelogOptions, Object> optionsMap = capsidExt.generateChangelogOptions;
        if (hasDefinedRepo) {
            optionsMap.putIfAbsent(GenerateChangelogOptions.USER, repoOwner);
            optionsMap.putIfAbsent(GenerateChangelogOptions.PROJECT, repoName);
            optionsMap.putIfAbsent(GenerateChangelogOptions.ISSUES_WITHOUT_LABELS, "false");
        } else CapsidPlugin.LOGGER.warn("WARN: Repository owner and name not specified");

        // first check for token in environment variables
        String token = System.getenv(TOKEN_ENV_VAR_NAME);
        if (Strings.isNullOrEmpty(token)) {
            // next check for token in project properties
            if (ext.has(TOKEN_PROPERTY_NAME)) {
                token = (String) ext.get(TOKEN_PROPERTY_NAME);
            }
            // don't pass token as null
            else token = "";
        }
        optionsMap.put(GenerateChangelogOptions.TOKEN, new String[] {token});

        List<String> command =
                new ArrayList<>(Arrays.asList("bundle", "exec", "github_changelog_generator"));
        for (Map.Entry<GenerateChangelogOptions, Object> entry : optionsMap.entrySet()) {
            String[] sValue;
            Object oValue = entry.getValue();
            if (oValue instanceof String[]) {
                sValue = (String[]) oValue;
            } else if (oValue instanceof String) {
                sValue = new String[] {(String) oValue};
            } else sValue = new String[] {oValue.toString()};

            command.add(entry.getKey().formatOption(sValue));
            command.addAll(Arrays.asList(sValue));
        }
        // windows platforms needs extra command tokens to work
        if (System.getProperty("os.name").startsWith("Windows")) {
            command.addAll(0, Arrays.asList("cmd", "/c"));
        }
        commandLine(command);

        // create Gemfile in root directory if one doesn't exist
        createGemfile(project);

        doFirst(
                task ->
                        CapsidPlugin.LOGGER.lifecycle(
                                String.format(
                                        "Generating changelog for %s/%s", repoOwner, repoName)));
    }
}
