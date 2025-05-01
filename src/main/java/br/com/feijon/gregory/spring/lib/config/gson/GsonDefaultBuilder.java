package br.com.feijon.gregory.spring.lib.config.gson;

import br.com.feijon.gregory.spring.lib.config.gson.adapter.JsonNodeTypeAdapter;
import br.com.feijon.gregory.spring.lib.config.gson.adapter.OptionalTypeAdapter;
import br.com.feijon.gregory.spring.lib.config.gson.factory.EnumUseAttributeInMarshallingTypeAdapterFactory;
import br.com.feijon.gregory.spring.lib.config.gson.factory.GsonRemoveUnusedDecimalTypeAdapterFactory;
import br.com.feijon.gregory.spring.lib.config.gson.strategy.GsonSerializationStrategy;
import br.com.feijon.gregory.spring.lib.utils.factory.FactoryUtil;
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
