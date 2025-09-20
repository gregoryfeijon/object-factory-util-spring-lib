package io.github.gregoryfeijon;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.gregoryfeijon.config.gson.adapter.OptionalTypeAdapter;
import io.github.gregoryfeijon.config.gson.factory.EnumUseAttributeInMarshallingTypeAdapterFactory;
import io.github.gregoryfeijon.config.gson.factory.GsonRemoveUnusedDecimalTypeAdapterFactory;
import io.github.gregoryfeijon.config.gson.strategy.GsonSerializationStrategy;
import io.github.gregoryfeijon.config.jackson.factory.EnumDeserializers;
import io.github.gregoryfeijon.config.jackson.factory.EnumSerializers;
import io.github.gregoryfeijon.domain.enums.SerializationType;
import io.github.gregoryfeijon.utils.serialization.adapter.GsonAdapter;
import io.github.gregoryfeijon.utils.serialization.adapter.JacksonAdapter;
import io.github.gregoryfeijon.utils.serialization.adapter.SerializerAdapter;
import io.github.gregoryfeijon.utils.serialization.adapter.SerializerProvider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.EnumMap;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class TestSerializerUtil {

    private static final ObjectMapper objectMapper;
    private static final Gson gson;

    static {
        objectMapper = buildObjectMapper();
        gson = buildGsonGmt();
    }

    static void configureGsonAndJacksonAdapter() {
        EnumMap<SerializationType, SerializerAdapter> adapters = new EnumMap<>(SerializationType.class);
        adapters.put(SerializationType.GSON, new GsonAdapter(gson));
        adapters.put(SerializationType.JACKSON, new JacksonAdapter(objectMapper));

        SerializerProvider.initialize(adapters, SerializationType.GSON);
    }

    private static ObjectMapper buildObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        mapper.registerModule(javaTimeModule);

        SimpleModule enumAttributeModule = new SimpleModule() {
            @Override
            public void setupModule(SetupContext context) {
                super.setupModule(context);
                context.addSerializers(new EnumSerializers());
                context.addDeserializers(new EnumDeserializers());
            }
        };

        mapper.registerModule(enumAttributeModule);

        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        return mapper;
    }


    private static Gson buildGsonGmt() {
        GsonBuilder builder = new GsonBuilder()
                .registerTypeAdapter(Optional.class, new OptionalTypeAdapter())
                .registerTypeAdapterFactory(new EnumUseAttributeInMarshallingTypeAdapterFactory())
                .registerTypeAdapterFactory(new GsonRemoveUnusedDecimalTypeAdapterFactory())
                .addSerializationExclusionStrategy(new GsonSerializationStrategy());
        return builder.create();
    }
}
