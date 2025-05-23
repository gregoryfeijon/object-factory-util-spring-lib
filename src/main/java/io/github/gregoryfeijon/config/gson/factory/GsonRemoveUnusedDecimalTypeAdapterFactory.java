package io.github.gregoryfeijon.config.gson.factory;

import io.github.gregoryfeijon.config.gson.adapter.GsonRemoveUnusedDecimalAdapter;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Component;

/**
 * A Gson TypeAdapterFactory that creates type adapters for number types.
 * <p>
 * This factory creates {@link GsonRemoveUnusedDecimalAdapter} instances for
 * number types to remove unnecessary decimal places during serialization.
 *
 * @author gregory.feijon
 */
@Component
public class GsonRemoveUnusedDecimalTypeAdapterFactory implements TypeAdapterFactory {

    /**
     * Creates a type adapter for the specified type.
     * <p>
     * If the type is a Number subclass, returns a {@link GsonRemoveUnusedDecimalAdapter}.
     * Otherwise, returns null to let Gson use its default adapter.
     *
     * @param gson The Gson instance
     * @param typeToken The type for which to create an adapter
     * @param <T> The type parameter
     * @return A type adapter for the specified type, or null if this factory doesn't handle the type
     */
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        Class<? super T> rawType = typeToken.getRawType();
        if (Number.class.isAssignableFrom(rawType)) {
            return new GsonRemoveUnusedDecimalAdapter<>(typeToken);
        }
        return null;
    }
}