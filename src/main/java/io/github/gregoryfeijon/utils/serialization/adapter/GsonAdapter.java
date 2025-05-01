package io.github.gregoryfeijon.utils.serialization.adapter;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public non-sealed class GsonAdapter implements SerializerAdapter {

    private final Gson gson;

    public GsonAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public String serialize(Object object) {
        return gson.toJson(object);
    }

    @Override
    public String serialize(Object object, Type type) {
        return gson.toJson(object, type);
    }

    @Override
    public <T> T deserialize(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }

    @Override
    public <T> T deserialize(String json, Type type) {
        return gson.fromJson(json, type);
    }
}