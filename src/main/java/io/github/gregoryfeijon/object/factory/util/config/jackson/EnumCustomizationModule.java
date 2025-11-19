package io.github.gregoryfeijon.object.factory.util.config.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import io.github.gregoryfeijon.object.factory.util.config.jackson.factory.EnumDeserializers;
import io.github.gregoryfeijon.object.factory.util.config.jackson.factory.EnumSerializers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Jackson module that registers custom enum serializers and deserializers.
 * <p>
 * This module integrates {@link EnumSerializers} and {@link EnumDeserializers}
 * into the Jackson serialization/deserialization process.
 */
@Slf4j
@RequiredArgsConstructor
public class EnumCustomizationModule extends Module {

    private final EnumSerializers enumSerializers;
    private final EnumDeserializers enumDeserializers;

    @Override
    public String getModuleName() {
        return "EnumCustomizationModule";
    }

    @Override
    public Version version() {
        return new Version(1, 0, 0, null, "io.github.gregoryfeijon", "enum-customization");
    }

    @Override
    public Object getTypeId() {
        // This provides a unique ID for the module that can be retrieved via getRegisteredModuleIds()
        return "EnumCustomizationModule";
    }

    @Override
    public void setupModule(SetupContext context) {
        log.debug("Setting up EnumCustomizationModule");

        // Register custom serializers and deserializers
        context.addSerializers(enumSerializers);
        context.addDeserializers(enumDeserializers);

        log.info("EnumCustomizationModule successfully registered");
    }
}