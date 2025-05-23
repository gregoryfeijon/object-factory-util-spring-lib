package io.github.gregoryfeijon.config.gson;

import io.github.gregoryfeijon.config.gson.adapter.JsonNodeTypeAdapter;
import io.github.gregoryfeijon.config.gson.adapter.OptionalTypeAdapter;
import io.github.gregoryfeijon.config.gson.factory.EnumUseAttributeInMarshallingTypeAdapterFactory;
import io.github.gregoryfeijon.config.gson.factory.GsonRemoveUnusedDecimalTypeAdapterFactory;
import io.github.gregoryfeijon.config.gson.strategy.GsonSerializationStrategy;
import io.github.gregoryfeijon.utils.factory.FactoryUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.GsonBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;

/**
 * Utility class for configuring a Gson builder with default settings.
 * <p>
 * This class provides a standardized way to configure GsonBuilder instances with
 * common type adapters, factories, and serialization strategies.
 *
 * @author gregory.feijon
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GsonDefaultBuilder {

    /**
     * Adds default configuration to a GsonBuilder.
     * <p>
     * The configuration includes:
     * <ul>
     *   <li>JsonNode adapter for Jackson compatibility</li>
     *   <li>Optional type adapter for Java's Optional support</li>
     *   <li>Enum attribute marshalling adapter for custom enum serialization</li>
     *   <li>Decimal formatting adapter to remove unnecessary decimal places</li>
     *   <li>Exclusion strategy for fields marked with {@code @Exclude}</li>
     * </ul>
     *
     * @param gsonBuilder The GsonBuilder to configure
     */
    public static void addDefaultConfig(GsonBuilder gsonBuilder) {
        gsonBuilder.registerTypeAdapter(JsonNode.class, FactoryUtil.getBean(JsonNodeTypeAdapter.class))
                .registerTypeAdapter(Optional.class, new OptionalTypeAdapter())
                .registerTypeAdapterFactory(new EnumUseAttributeInMarshallingTypeAdapterFactory())
                .registerTypeAdapterFactory(new GsonRemoveUnusedDecimalTypeAdapterFactory())
                .addSerializationExclusionStrategy(new GsonSerializationStrategy());
    }
}