package io.pzstorm.capsid.property;

import io.pzstorm.capsid.PluginUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CapsidPropertyTest extends PluginUnitTest {

    @Test
    void shouldBuildCapsidPropertyWithCorrectValues() {
        String name = "testProperty";
        String comment = "testComment";

        CapsidProperty<String> property =
                new CapsidProperty.Builder<>(name, String.class)
                        .withComment(comment)
                        .isRequired(false)
                        .build();

        Assertions.assertEquals(name, property.name);
        Assertions.assertEquals(String.class, property.type);
        Assertions.assertEquals(comment, property.comment);
        Assertions.assertFalse(property.required);
    }
}
