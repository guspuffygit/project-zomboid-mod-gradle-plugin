package io.pzstorm.capsid.property;

import com.google.common.collect.ImmutableSet;
import io.pzstorm.capsid.CapsidPlugin;
import io.pzstorm.capsid.util.SemanticVersion;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import org.gradle.api.Project;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

/** This class holds properties related to tracking versions. */
public class VersionProperties extends CapsidProperties {

    /** {@code ZomboidDoc} version registered last time {@code ZomboidDoc} task was run. */
    public static final CapsidProperty<SemanticVersion> LAST_ZDOC_VERSION;

    private static final VersionProperties INSTANCE = new VersionProperties();
    private static final @Unmodifiable Set<CapsidProperty<?>> PROPERTIES;

    static {
        LAST_ZDOC_VERSION =
                new CapsidProperty.Builder<>("lastZDocVersion", SemanticVersion.class)
                        .withComment(
                                "ZomboidDoc version registered last time ZomboidDoc task was run")
                        .withDefaultValue(new SemanticVersion("0.0.0"))
                        .build();

        PROPERTIES = ImmutableSet.of(LAST_ZDOC_VERSION);
    }

    private VersionProperties() {
        super(Paths.get("version.properties"));
    }

    /** Returns singleton instance of {@link VersionProperties}. */
    public static VersionProperties get() {
        return INSTANCE;
    }

    @Override
    @Contract(pure = true)
    public @Unmodifiable Set<CapsidProperty<?>> getProperties() {
        return PROPERTIES;
    }

    /**
     * Write properties with comments to {@code version.properties} file.
     *
     * @param project {@link Project} instance used to resolve the {@code File}.
     * @throws IOException when an I/O exception occurred while writing to file.
     */
    @Override
    public void writeToFile(Project project) throws IOException {

        File target = getFile(project);
        if (!target.exists() && !target.createNewFile()) {
            throw new IOException("Unable to create 'version.properties' file in root directory");
        }
        try (Writer writer =
                Files.newBufferedWriter(getFile(project).toPath(), StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();

            // write properties and their comments to file
            for (CapsidProperty<?> property : PROPERTIES) {
                String value = "";
                Object oProperty = property.findProperty(project);
                if (oProperty != null) {
                    value = oProperty.toString();
                } else if (property.required) {
                    CapsidPlugin.LOGGER.warn("WARN: Missing property value " + property.name);
                }
                String comment = property.comment;
                if (comment != null && !comment.isEmpty()) {
                    sb.append('#').append(comment).append('\n');
                }
                sb.append(property.name).append('=').append(value).append("\n\n");
            }
            writer.write(sb.toString());
        }
    }
}
