package io.pzstorm.capsid.zomboid.task;

import io.pzstorm.capsid.ProjectProperty;
import javax.inject.Inject;

/** This task decompiles Project Zomboid Java libraries. */
public class DecompileZomboidLibrariesTask extends DecompileJavaTask {

    @Inject
    public DecompileZomboidLibrariesTask() {
        super(
                ProjectProperty.ZOMBOID_LIBRARIES.getSupplier(),
                ProjectProperty.ZOMBOID_LIBRARY_SOURCES_DIR.getSupplier());
    }
}
