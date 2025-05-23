package io.github.gregoryfeijon.config.gson.factory;

import io.github.gregoryfeijon.config.gson.adapter.EnumUseAttributeInMarshallingTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Component;

/**
 * A Gson TypeAdapterFactory that creates type adapters for enum types.
 * <p>
 * This factory creates {@link EnumUseAttributeInMarshallingTypeAdapter} instances for
 * enum types to enable serialization and deserialization based on enum attributes
 * rather than enum names.
 *
 * @author gregory.feijon
 */
@Component
public class EnumUseAttributeInMarshallingTypeAdapterFactory implements TypeAdapterFactory {

    /**
     * Creates a type adapter for the specified type.
     * <p>
     * If the type is an enum, returns an {@link EnumUseAttributeInMarshallingTypeAdapter}.
     * Otherwise, returns null to let Gson use its default adapter.
     *
     * @param gson The Gson instance
     * @param type The type for which to create an adapter
     * @param <T> The type parameter
     * @return A type adapter for the specified type, or null if this factory doesn't handle the type
     */
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<? super T> rawType = type.getRawType();
        if (rawType.isEnum()) {
            return new EnumUseAttributeInMarshallingTypeAdapter<>(type);
        }
        return null;
    }
}