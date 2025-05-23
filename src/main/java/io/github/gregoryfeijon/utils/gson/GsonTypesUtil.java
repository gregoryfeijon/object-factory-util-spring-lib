package io.github.gregoryfeijon.utils.gson;

import com.google.gson.reflect.TypeToken;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Utility class for creating Gson type tokens and handling generic types.
 * <p>
 * This class provides methods to create type information for generic classes,
 * which is useful for Gson serialization and deserialization of generic collections.
 *
 * @author gregory.feijon
 * @since 28/11/2023
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GsonTypesUtil {

    /**
     * Creates a parameterized type with a single generic parameter.
     * <p>
     * This method is useful for creating type tokens for classes with a single
     * generic parameter, such as {@code Container<Item>}.
     *
     * @param rawClass The raw class (e.g., Container)
     * @param genClass The generic parameter class (e.g., Item)
     * @return A Type representing the parameterized type
     */
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

    /**
     * Creates a parameterized type for a List with the specified element type.
     * <p>
     * This method is a convenience wrapper for creating a type token for
     * {@code List<T>} where T is the specified class.
     *
     * @param clazz The class of the list elements
     * @return A Type representing {@code List<clazz>}
     */
    public static Type getListType(Class<?> clazz) {
        return TypeToken.getParameterized(List.class, clazz).getType();
    }

    /**
     * Creates a parameterized type for a Set with the specified element type.
     * <p>
     * This method is a convenience wrapper for creating a type token for
     * {@code Set<T>} where T is the specified class.
     *
     * @param clazz The class of the set elements
     * @return A Type representing {@code Set<clazz>}
     */
    public static Type getSetType(Class<?> clazz) {
        return TypeToken.getParameterized(Set.class, clazz).getType();
    }

    /**
     * Creates a parameterized type for a Collection with the specified element type.
     * <p>
     * This method is a convenience wrapper for creating a type token for
     * {@code Collection<T>} where T is the specified class.
     *
     * @param clazz The class of the collection elements
     * @return A Type representing {@code Collection<clazz>}
     */
    public static Type getCollectionType(Class<?> clazz) {
        return TypeToken.getParameterized(Collection.class, clazz).getType();
    }
}