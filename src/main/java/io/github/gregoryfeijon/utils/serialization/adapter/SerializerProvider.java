package io.github.gregoryfeijon.utils.serialization.adapter;

import io.github.gregoryfeijon.domain.enums.SerializationType;
import io.github.gregoryfeijon.utils.factory.FactoryUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.EnumMap;
import java.util.Map;

/**
 * Provider class for serialization adapters.
 * <p>
 * This class manages and provides access to serialization adapters for different
 * serialization frameworks (Gson, Jackson). It follows a singleton pattern and
 * can be initialized with custom adapters or with default ones.
 *
 * @author gregory.feijon
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SerializerProvider {

    private static final Map<SerializationType, SerializerAdapter> ADAPTERS = new EnumMap<>(SerializationType.class);
    private static volatile SerializationType defaultType;

    /**
     * Initializes the provider with the specified adapters and default type.
     * <p>
     * This method should be called once during application startup. If the provider
     * has already been initialized, this method has no effect.
     *
     * @param adapters The map of serialization types to their corresponding adapters
     * @param defaultType The default serialization type to use
     */
    public static void initialize(Map<SerializationType, SerializerAdapter> adapters, SerializationType defaultType) {
        if (ADAPTERS.isEmpty()) {
            ADAPTERS.putAll(adapters);
            SerializerProvider.defaultType = defaultType;
        }
    }

    /**
     * Initializes the provider with default adapters if it hasn't been initialized yet.
     * <p>
     * This method attempts to create Gson and Jackson adapters using beans from the
     * Spring application context.
     *
     * @throws IllegalStateException If initialization fails due to missing required beans
     */
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
                        "SerializerProvider Initialization failed. Verify if there's any bean called 'gson' or 'objectMapper' configured properly!",
                        e
                );
            }
        }
    }

    /**
     * Gets the default serializer adapter.
     * <p>
     * If the provider hasn't been initialized yet, it will be initialized with default adapters.
     *
     * @return The default serializer adapter
     */
    public static SerializerAdapter getAdapter() {
        if (ADAPTERS.isEmpty()) {
            initializeIfEmpty();
        }
        return ADAPTERS.get(defaultType);
    }

    /**
     * Gets a serializer adapter for a specific serialization type.
     * <p>
     * If the provider hasn't been initialized yet, it will be initialized with default adapters.
     *
     * @param type The serialization type
     * @return The serializer adapter for the specified type
     */
    public static SerializerAdapter getAdapter(SerializationType type) {
        if (ADAPTERS.isEmpty()) {
            initializeIfEmpty();
        }
        return ADAPTERS.get(type);
    }
}