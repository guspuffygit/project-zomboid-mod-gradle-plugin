package io.pzstorm.capsid.property.validator;

public class PropertyValidators {

    /** Validates if property represents a path to an existing directory. */
    public static final DirectoryPathValidator DIRECTORY_PATH_VALIDATOR =
            new DirectoryPathValidator();

    /** Validates if property represents a valid Github {@code URL}. */
    public static final GithubUrlValidator GITHUB_URL_VALIDATOR = new GithubUrlValidator();
}
