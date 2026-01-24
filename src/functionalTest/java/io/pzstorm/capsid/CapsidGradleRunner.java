package io.pzstorm.capsid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.gradle.testkit.runner.internal.DefaultGradleRunner;

public class CapsidGradleRunner extends DefaultGradleRunner {

    public static CapsidGradleRunner create() {
        return new CapsidGradleRunner();
    }

    @Override
    public CapsidGradleRunner withArguments(String... arguments) {
        List<String> argumentList = new ArrayList<>(getArguments());
        argumentList.addAll(Arrays.asList(arguments));
        return (CapsidGradleRunner) this.withArguments(argumentList);
    }
}
