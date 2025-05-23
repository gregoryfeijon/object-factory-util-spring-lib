package io.github.gregoryfeijon.domain.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumeration of supported serialization types in the framework.
 * <p>
 * This enum defines the serialization frameworks that can be used with
 * {@link io.github.gregoryfeijon.utils.serialization.adapter.SerializerProvider}.
 * Each value represents a different serialization approach, with associated
 * adapters implementing the serialization and deserialization logic.
 *
 * @author gregory.feijon
 * @since 04/04/2025
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum SerializationType {

    /**
     * Google's Gson serialization framework.
     * <p>
     * Uses {@link io.github.gregoryfeijon.utils.serialization.adapter.GsonAdapter}
     * for serialization operations.
     */
    GSON("gson"),

    /**
     * Jackson serialization framework.
     * <p>
     * Uses {@link io.github.gregoryfeijon.utils.serialization.adapter.JacksonAdapter}
     * for serialization operations.
     */
    JACKSON("jackson");

    /**
     * A string description of the serialization type, typically used for configuration.
     */
    private final String description;
}
