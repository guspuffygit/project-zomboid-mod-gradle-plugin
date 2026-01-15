
package io.pzstorm.capsid.setup;

import io.pzstorm.capsid.PluginUnitTest;
import io.pzstorm.capsid.property.CapsidProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LocalPropertiesTest extends PluginUnitTest {

    @Test
    void shouldGetAllLocalPropertiesByName() {
        LocalProperties localProperties = LocalProperties.get();
        for (CapsidProperty<?> value : localProperties.getProperties()) {
            Assertions.assertEquals(value, localProperties.getProperty(value.name));
        }
    }
}
