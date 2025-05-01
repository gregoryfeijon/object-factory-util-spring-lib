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

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GsonDefaultBuilder {

    public static void addDefaultConfig(GsonBuilder gsonBuilder) {
        gsonBuilder.registerTypeAdapter(JsonNode.class, FactoryUtil.getBean(JsonNodeTypeAdapter.class))
                .registerTypeAdapter(Optional.class, new OptionalTypeAdapter())
                .registerTypeAdapterFactory(new EnumUseAttributeInMarshallingTypeAdapterFactory())
                .registerTypeAdapterFactory(new GsonRemoveUnusedDecimalTypeAdapterFactory())
                .addSerializationExclusionStrategy(new GsonSerializationStrategy());
    }
}
