
package io.pzstorm.capsid;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.jspecify.annotations.NullMarked;

/** Standard contract for all plugin task implementations. */
@NullMarked
public interface CapsidTask extends Task {

    /**
     * Apply the configuration to this task.
     *
     * @param group task group which this task belongs to.
     * @param description description for this task.
     * @param project {@code Project} configuring the task.
     */
    default void configure(String group, String description, Project project) {
        setGroup(group);
        setDescription(description);
    }
}
