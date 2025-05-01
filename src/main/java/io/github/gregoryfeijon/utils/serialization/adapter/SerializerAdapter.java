package io.github.gregoryfeijon.utils.serialization.adapter;

import java.lang.reflect.Type;

public sealed interface SerializerAdapter permits GsonAdapter, JacksonAdapter {

    String serialize(Object object);

    String serialize(Object object, Type type);

    <T> T deserialize(String json, Class<T> type);

    <T> T deserialize(String json, Type type);
}
