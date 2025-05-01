package br.com.feijon.gregory.spring.lib.utils.gson;

import com.google.gson.reflect.TypeToken;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 28/11/2023 às 19:18
 *
 * @author gregory.feijon
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GsonTypesUtil {

    public static Type getType(Class<?> rawClass, Class<?> genClass) {
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{genClass};
            }

            @Override
            public Type getRawType() {
                return rawClass;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }

    public static Type getListType(Class<?> clazz) {
        return TypeToken.getParameterized(List.class, clazz).getType();
    }

    public static Type getSetType(Class<?> clazz) {
        return TypeToken.getParameterized(Set.class, clazz).getType();
    }

    public static Type getCollectionType(Class<?> clazz) {
        return TypeToken.getParameterized(Collection.class, clazz).getType();
    }
}
