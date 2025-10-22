package io.github.gregoryfeijon.config.gson.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

import java.io.IOException;

/**
 * Type adapter for Gson that handles Hibernate proxy objects during serialization.
 * <p>
 * This adapter unwraps Hibernate proxied objects and delegates their serialization
 * to the appropriate TypeAdapter of the actual entity class. This prevents errors
 * when attempting to serialize lazy-loaded entities that haven't been initialized.
 * <p>
 * When a Hibernate proxy is encountered during serialization, this adapter:
 * <ul>
 *   <li>Retrieves the real class type of the proxied entity using {@link Hibernate#getClass}</li>
 *   <li>Obtains the implementation instance from the proxy's lazy initializer</li>
 *   <li>Delegates serialization to the TypeAdapter registered for the real class</li>
 * </ul>
 * <p>
 * <strong>Important:</strong> This adapter requires an active Hibernate session to initialize
 * lazy-loaded proxies. Ensure serialization occurs within a transactional context to avoid
 * {@link org.hibernate.LazyInitializationException}.
 *
 * @param <T> The type being adapted (must be assignable from HibernateProxy)
 * @author gregory.feijon
 * @see HibernateProxy
 * @see io.github.gregoryfeijon.config.gson.factory.HibernateProxyTypeAdapterFactory
 */
@Slf4j
@RequiredArgsConstructor
public class HibernateProxyTypeAdapter<T> extends TypeAdapter<T> {

    private final Gson gson;

    /**
     * Writes the JSON representation of a Hibernate proxy.
     * <p>
     * Unwraps the proxy to obtain the real entity and delegates serialization
     * to the appropriate type adapter for that entity's class.
     *
     * @param out   The JSON writer
     * @param value The Hibernate proxy value to write
     * @throws IOException If an I/O error occurs during writing
     */
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void write(JsonWriter out, T value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        if (value instanceof HibernateProxy hibernateProxy) {
            Class<?> baseType = Hibernate.getClass(hibernateProxy);
            TypeAdapter delegate = gson.getAdapter(TypeToken.get(baseType));
            Object unproxiedValue = hibernateProxy.getHibernateLazyInitializer().getImplementation();

            delegate.write(out, unproxiedValue);

            log.debug("Successfully serialized Hibernate proxy of type: {}", baseType.getSimpleName());
        } else {
            log.warn("Expected HibernateProxy but received: {}", value.getClass().getName());
            gson.toJson(value, value.getClass(), out);
        }
    }

    /**
     * Reads a JSON value and converts it to the target type.
     * <p>
     * <strong>Note:</strong> Deserialization of Hibernate proxies is not supported.
     * Proxies are a runtime construct and cannot be recreated from JSON.
     * Deserialize directly to the entity class instead.
     *
     * @param in The JSON reader
     * @return Nothing - this method always throws UnsupportedOperationException
     * @throws UnsupportedOperationException Always thrown as proxy deserialization is not supported
     */
    @Override
    public T read(JsonReader in) {
        throw new UnsupportedOperationException(
                "Deserialization of Hibernate proxies is not supported. " +
                        "Deserialize to the actual entity class instead."
        );
    }
}