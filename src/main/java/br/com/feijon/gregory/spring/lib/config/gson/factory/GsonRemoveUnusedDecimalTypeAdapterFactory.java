package br.com.feijon.gregory.spring.lib.config.gson.factory;

import br.com.feijon.gregory.spring.lib.config.gson.adapter.GsonRemoveUnusedDecimalAdapter;
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

