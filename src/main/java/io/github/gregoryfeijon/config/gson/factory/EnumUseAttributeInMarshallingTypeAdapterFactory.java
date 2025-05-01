package io.github.gregoryfeijon.config.gson.factory;

import io.github.gregoryfeijon.config.gson.adapter.EnumUseAttributeInMarshallingTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Component;

@Component
public class EnumUseAttributeInMarshallingTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<? super T> rawType = type.getRawType();
        if (rawType.isEnum()) {
            return new EnumUseAttributeInMarshallingTypeAdapter<>(type);
        }
        return null;
    }
}

