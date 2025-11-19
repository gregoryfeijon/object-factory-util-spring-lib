package io.github.gregoryfeijon.object.factory.util.config.gson.factory;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import io.github.gregoryfeijon.object.factory.util.config.gson.adapter.HibernateProxyTypeAdapter;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.stereotype.Component;

/**
 * A Gson TypeAdapterFactory that creates type adapters for Hibernate proxy types.
 * <p>
 * This factory creates {@link HibernateProxyTypeAdapter} instances for Hibernate proxy
 * objects to enable proper serialization by unwrapping the proxy and delegating to
 * the actual entity's type adapter.
 * <p>
 * This prevents "Attempted to serialize java.lang.Class: org.hibernate.proxy.HibernateProxy"
 * errors when serializing entities with lazy-loaded relationships.
 *
 * @author gregory.feijon
 * @see HibernateProxy
 * @see HibernateProxyTypeAdapter
 */
@Component
public class HibernateProxyTypeAdapterFactory implements TypeAdapterFactory {

    /**
     * Creates a type adapter for the specified type.
     * <p>
     * If the type is assignable from HibernateProxy, returns a {@link HibernateProxyTypeAdapter}.
     * Otherwise, returns null to let Gson use its default adapter.
     *
     * @param gson The Gson instance
     * @param type The type for which to create an adapter
     * @param <T>  The type parameter
     * @return A type adapter for the specified type, or null if this factory doesn't handle the type
     */
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<? super T> rawType = type.getRawType();
        if (HibernateProxy.class.isAssignableFrom(rawType)) {
            return new HibernateProxyTypeAdapter<>(gson);
        }
        return null;
    }
}