package io.github.gregoryfeijon.object.factory.util.domain.enums;

import io.github.gregoryfeijon.object.factory.util.utils.serialization.adapter.GsonAdapter;
import io.github.gregoryfeijon.object.factory.util.utils.serialization.adapter.JacksonAdapter;
import io.github.gregoryfeijon.object.factory.util.utils.serialization.adapter.SerializerProvider;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumeration of supported serialization types in the framework.
 * <p>
 * This enum defines the serialization frameworks that can be used with
 * {@link SerializerProvider}.
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
     * Uses {@link GsonAdapter}
     * for serialization operations.
     */
    GSON("gson"),

    /**
     * Jackson serialization framework.
     * <p>
     * Uses {@link JacksonAdapter}
     * for serialization operations.
     */
    JACKSON("jackson");

    /**
     * A string description of the serialization type, typically used for configuration.
     */
    private final String description;
}
