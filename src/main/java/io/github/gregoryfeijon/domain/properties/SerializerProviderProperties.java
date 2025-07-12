package io.github.gregoryfeijon.domain.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration properties for the SerializerProvider.
 * <p>
 * This class holds configuration settings that determine the behavior of the
 * {@link io.github.gregoryfeijon.utils.serialization.adapter.SerializerProvider}.
 * It can be loaded from external configuration sources (e.g., application.properties
 * or application.yml) to customize serialization behavior.
 *
 * @author gregory.feijon
 */
@Getter
@Setter
public class SerializerProviderProperties {

    /**
     * Determines whether the SerializerProvider is enabled.
     * <p>
     * When set to false, serialization operations may fall back to default behavior
     * or be disabled entirely, depending on the implementation.
     */
    private boolean enabled;

    /**
     * Specifies the serialization type to use.
     * <p>
     * This should correspond to one of the values defined in {@link io.github.gregoryfeijon.domain.enums.SerializationType}.
     * Common values are "gson" and "jackson".
     */
    private String type;
}
