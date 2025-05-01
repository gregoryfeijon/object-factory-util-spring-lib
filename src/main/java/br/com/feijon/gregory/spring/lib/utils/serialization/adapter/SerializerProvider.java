package br.com.feijon.gregory.spring.lib.utils.serialization.adapter;

import br.com.feijon.gregory.spring.lib.domain.enums.SerializationType;
import br.com.feijon.gregory.spring.lib.utils.factory.FactoryUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.EnumMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SerializerProvider {

    private static final Map<SerializationType, SerializerAdapter> ADAPTERS = new EnumMap<>(SerializationType.class);
    private static volatile SerializationType defaultType;

    public static void initialize(Map<SerializationType, SerializerAdapter> adapters, SerializationType defaultType) {
        if (ADAPTERS.isEmpty()) {
            ADAPTERS.putAll(adapters);
            SerializerProvider.defaultType = defaultType;
        }
    }

    public static synchronized void initializeIfEmpty() {
        if (ADAPTERS.isEmpty()) {
            try {
                EnumMap<SerializationType, SerializerAdapter> adapters = new EnumMap<>(SerializationType.class);
                adapters.put(SerializationType.GSON, new GsonAdapter(FactoryUtil.getBeanFromName("gson", Gson.class)));
                adapters.put(SerializationType.JACKSON, new JacksonAdapter(FactoryUtil.getBean(ObjectMapper.class)));

                ADAPTERS.putAll(adapters);
                defaultType = SerializationType.GSON;

            } catch (Exception e) {
                throw new IllegalStateException(
                        "SerializerProvider Inicialization failed. Verify if there's any bean called 'gson' or 'objectMapper' configurated properly!",
                        e
                );
            }
        }
    }

    public static SerializerAdapter getAdapter() {
        if (ADAPTERS.isEmpty()) {
            initializeIfEmpty();
        }
        return ADAPTERS.get(defaultType);
    }

    public static SerializerAdapter getAdapter(SerializationType type) {
        if (ADAPTERS.isEmpty()) {
            initializeIfEmpty();
        }
        return ADAPTERS.get(type);
    }
}