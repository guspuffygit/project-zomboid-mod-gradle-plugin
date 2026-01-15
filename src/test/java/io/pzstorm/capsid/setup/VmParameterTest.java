
package io.pzstorm.capsid.setup;

import io.pzstorm.capsid.PluginUnitTest;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class VmParameterTest extends PluginUnitTest {

    @Test
    void shouldBuildVmParameterInstanceWithDefaultValues() {
        VmParameter parameters = new VmParameter.Builder().build();

        Assertions.assertFalse(parameters.isDebug);
        Assertions.assertTrue(parameters.steamIntegration);
        Assertions.assertEquals(1, parameters.zNetLog);

        Assertions.assertTrue(parameters.useConcMarkSweepGC);
        Assertions.assertFalse(parameters.createMinidumpOnCrash);
        Assertions.assertFalse(parameters.omitStackTraceInFastThrow);

        Assertions.assertEquals(1800, parameters.xms);
        Assertions.assertEquals(2048, parameters.xmx);
    }

    @Test
    void shouldBuildVmParameterInstanceWithCustomValues() {
        String[] javaLibPaths = new String[] {"/home/libs/java", "/home/java/libs"};
        String[] lwjglPaths = new String[] {"/home/libs/lwjgl1", "/home/lwjgl/libs"};

        VmParameter parameters =
                new VmParameter.Builder()
                        .withDebug(true)
                        .withSteamIntegration(false)
                        .withNetworkLogging(0)
                        .withConcurrentMarkSweepCollection(false)
                        .withMinidumpOnCrash(false)
                        .withOmittingStackTraceInFastThrow(false)
                        .withJavaLibraryPaths(javaLibPaths)
                        .withLwjglLibraryPaths(lwjglPaths)
                        .withInitialMemoryAllocation(250)
                        .withMaximumMemoryAllocation(4096)
                        .build();

        Assertions.assertTrue(parameters.isDebug);
        Assertions.assertFalse(parameters.steamIntegration);
        Assertions.assertEquals(0, parameters.zNetLog);

        Assertions.assertFalse(parameters.useConcMarkSweepGC);
        Assertions.assertFalse(parameters.createMinidumpOnCrash);
        Assertions.assertFalse(parameters.omitStackTraceInFastThrow);

        Assertions.assertEquals(javaLibPaths, parameters.javaLibraryPaths);
        Assertions.assertEquals(lwjglPaths, parameters.lwjglLibraryPaths);

        Assertions.assertEquals(250, parameters.xms);
        Assertions.assertEquals(4096, parameters.xmx);
    }

    @Test
    @SuppressWarnings("SpellCheckingInspection")
    void shouldCorrectlyFormatAdvancedRuntimeOption() {
        Map<String, Boolean> advancedOptions = new HashMap<>();
        advancedOptions.put("useConcMarkSweepGC", true);
        advancedOptions.put("createMinidumpOnCrash", false);
        advancedOptions.put("omitStackTraceInFastThrow", true);

        for (Map.Entry<String, Boolean> entry : advancedOptions.entrySet()) {
            String optionName = entry.getKey();
            boolean flag = entry.getValue();

            String expected = "-XX:" + (flag ? '+' : '-') + optionName;
            String actual = VmParameter.formatAdvancedRuntimeOption(optionName, flag);
            Assertions.assertEquals(expected, actual);
        }
    }

    @Test
    @SuppressWarnings("SpellCheckingInspection")
    void shouldCorrectlyDisplayAsString() {
        String[] javaLibPaths = new String[] {"/home/libs/java", "/home/java/libs"};
        String[] lwjglPaths = new String[] {"/home/libs/lwjgl1", "/home/lwjgl/libs"};

        VmParameter parameters =
                new VmParameter.Builder()
                        .withDebug(true)
                        .withSteamIntegration(false)
                        .withNetworkLogging(0)
                        .withConcurrentMarkSweepCollection(false)
                        .withMinidumpOnCrash(false)
                        .withOmittingStackTraceInFastThrow(false)
                        .withJavaLibraryPaths(javaLibPaths)
                        .withLwjglLibraryPaths(lwjglPaths)
                        .withInitialMemoryAllocation(250)
                        .withMaximumMemoryAllocation(4096)
                        .build();

        String delimiter = VmParameter.getPathDelimiter();
        String expected =
                "-Ddebug -Dzomboid.steam=0 -Dzomboid.znetlog=0 "
                        + "-XX:-UseConcMarkSweepGC -XX:-CreateMinidumpOnCrash "
                        + "-XX:-OmitStackTraceInFastThrow -Xms250m -Xmx4096m "
                        + "-Djava.library.path=/home/libs/java"
                        + delimiter
                        + "/home/java/libs "
                        + "-Dorg.lwjgl.librarypath=/home/libs/lwjgl1"
                        + delimiter
                        + "/home/lwjgl/libs";

        Assertions.assertEquals(expected, parameters.toString());
    }
}
