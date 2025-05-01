package io.github.gregoryfeijon.config.gson.factory;

import io.github.gregoryfeijon.config.gson.adapter.GsonRemoveUnusedDecimalAdapter;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Component;

@Component
public class GsonRemoveUnusedDecimalTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        Class<? super T> rawType = typeToken.getRawType();
        if (Number.class.isAssignableFrom(rawType)) {
            return new GsonRemoveUnusedDecimalAdapter<>(typeToken);
        }
        return null;
    }
}

