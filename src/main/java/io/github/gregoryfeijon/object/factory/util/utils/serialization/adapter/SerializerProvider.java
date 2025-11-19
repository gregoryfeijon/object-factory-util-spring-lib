package io.github.gregoryfeijon.object.factory.util.utils.serialization.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.github.gregoryfeijon.object.factory.commons.utils.factory.FactoryUtil;
import io.github.gregoryfeijon.object.factory.util.domain.enums.SerializationType;
import io.github.gregoryfeijon.object.factory.util.domain.properties.SerializerProviderProperties;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
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
     * @param adapters    The map of serialization types to their corresponding adapters
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
        if (!isEnabled()) {
            return;
        }
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

    private static boolean isEnabled() {
        SerializerProviderProperties props = FactoryUtil.getBean(SerializerProviderProperties.class);
        if (props.isEnabled()) {
            return true;
        }
        log.warn(
                "SerializerProvider is disabled! " +
                        "No adapters will be initialized and ObjectFactoryUtil will not be available."
        );
        return false;
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
        SerializerAdapter adapter = ADAPTERS.get(defaultType);
        if (adapter == null) {
            throw new IllegalStateException("No default adapter found! Verify SerializerProvider initialization.");
        }
        return adapter;
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