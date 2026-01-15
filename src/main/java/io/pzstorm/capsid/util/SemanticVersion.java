
package io.pzstorm.capsid.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.gradle.api.InvalidUserDataException;

/**
 * This object represents a valid semantic version.
 *
 * @see <a href="https://semver.org/">Semantic Versioning</a>
 */
public class SemanticVersion {

    public static final Comparator COMPARATOR = new Comparator();
    private static final Pattern SEM_VER = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)(-(.*))?$");

    public final Integer major, minor, patch;
    public final String classifier;

    /**
     * Creates a new semantic version instance from given {@code String}.
     *
     * @throws InvalidUserDataException if given semantic version is malformed.
     */
    public SemanticVersion(String version) {
        Matcher matcher = SEM_VER.matcher(version);
        if (!matcher.find()) {
            throw new InvalidUserDataException("Malformed semantic version '" + version + '\'');
        }
        this.major = Integer.valueOf(matcher.group(1));
        this.minor = Integer.valueOf(matcher.group(2));
        this.patch = Integer.valueOf(matcher.group(3));

        String sClassifier = matcher.group(4);
        this.classifier = sClassifier != null ? matcher.group(5) : "";
    }

    @Override
    public String toString() {
        String sClassifier = !classifier.isEmpty() ? '-' + classifier : classifier;
        return String.format("%d.%d.%d%s", major, minor, patch, sClassifier);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SemanticVersion)) {
            return false;
        }
        SemanticVersion that = (SemanticVersion) o;
        if (new Comparator().compare(this, that) != 0) {
            return false;
        }
        return classifier.equals(that.classifier);
    }

    @Override
    public int hashCode() {
        int result = 31 * major.hashCode() + minor.hashCode();
        return 31 * (31 * result + patch.hashCode()) + classifier.hashCode();
    }

    public static class Comparator implements java.util.Comparator<SemanticVersion> {

        @Override
        public int compare(SemanticVersion o1, SemanticVersion o2) {
            if (!o1.major.equals(o2.major)) {
                return o1.major.compareTo(o2.major);
            }
            if (!o1.minor.equals(o2.minor)) {
                return o1.minor.compareTo(o2.minor);
            }
            return o1.patch.compareTo(o2.patch);
        }
    }
}
