package io.github.gregoryfeijon.utils.serialization;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.sql.Time;
import java.text.Format;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReflectionTypeUtils {

    private static final Set<Class<?>> WRAPPER_TYPES;
    private static final Map<Class<?>, Object> DEFAULT_VALUES = new HashMap<>();
    private static final Map<Class<?>, Boolean> SIMPLE_TYPE_CACHE = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Boolean> WRAPPER_CACHE = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Boolean> ARRAY_PRIMITIVE_OR_WRAPPER_CACHE = new ConcurrentHashMap<>();

    static {
        WRAPPER_TYPES = getWrapperTypes();
        initDefaultValues();
    }

    /**
     * <strong>Método para obter o tipo de {@linkplain Collection} utilizado no
     * atributo.</strong>
     *
     * @param genericType - {@linkplain Type}
     * @return {@linkplain Class}&lt;?&gt;
     */
    public static Class<?> getRawType(Type genericType) throws ClassNotFoundException {
        if (genericType instanceof Class<?> clazz) {
            return clazz;
        }
        if (genericType instanceof ParameterizedType parameterizedType) {
            Type rawType = parameterizedType.getRawType();
            return ClassUtils.getClass(rawType.getTypeName());
        }
        if (genericType instanceof GenericArrayType genericArrayType) {
            Class<?> componentType = getRawType(genericArrayType.getGenericComponentType());
            return Array.newInstance(componentType, 0).getClass();
        }
        if (genericType instanceof TypeVariable<?> typeVariable) {
            Type[] bounds = typeVariable.getBounds();
            if (bounds.length > 0) {
                return getRawType(bounds[0]);
            }
            throw new IllegalArgumentException(
                    "TypeVariable without bounds: " + typeVariable.getName()
            );
        }
        if (genericType instanceof WildcardType wildcardType) {
            Type[] upperBounds = wildcardType.getUpperBounds();
            if (upperBounds.length > 0) {
                return getRawType(upperBounds[0]);
            }
            throw new IllegalArgumentException(
                    "WildcardType without upper bounds: " + wildcardType
            );
        }
        throw new IllegalArgumentException(
                "Unsupported Type implementation: " + genericType.getClass().getName()
        );
    }

    /**
     * <strong>Método para verificar se é um tipo primitivo ou
     * {@linkplain Enum}.</strong>
     *
     * @param type - {@linkplain Class}&lt;?&gt;
     * @return boolean
     */
    public static boolean isPrimitiveOrEnum(Class<?> type) {
        return type.isPrimitive() || type.isEnum();
    }

    /**
     * <strong>Método para verificar se é uma {@linkplain Collection} ou um
     * {@linkplain Map}.</strong>
     *
     * @param clazz - {@linkplain Class}&lt;?&gt;
     * @return boolean
     */
    public static boolean isClassMapCollection(Class<?> clazz) {
        return isCollection(clazz) || isMap(clazz);
    }

    /**
     * <strong>Método para verificar se é uma {@linkplain Collection}.</strong>
     *
     * @param clazz - {@linkplain Class}&lt;?&gt;
     * @return boolean
     */
    public static boolean isCollection(Class<?> clazz) {
        return Collection.class.isAssignableFrom(clazz);
    }

    /**
     * <strong>Método para verificar se é um {@linkplain Map}.</strong>
     *
     * @param clazz - {@linkplain Class}&lt;?&gt;
     * @return boolean
     */
    public static boolean isMap(Class<?> clazz) {
        return Map.class.isAssignableFrom(clazz);
    }

    public static boolean isSimpleType(Class<?> clazz) {
        return SIMPLE_TYPE_CACHE.computeIfAbsent(clazz, c ->
                c.isPrimitive()
                        || isWrapperType(c)
                        || isArrayOfPrimitiveOrWrapper(c)
        );
    }

    private static boolean isArrayOfPrimitiveOrWrapper(Class<?> clazz) {
        return ARRAY_PRIMITIVE_OR_WRAPPER_CACHE.computeIfAbsent(clazz, c -> {
            if (!c.isArray()) {
                return false;
            }
            Class<?> componentType = c.getComponentType();
            return componentType.isPrimitive() || isWrapperType(componentType);
        });
    }

    /**
     * <strong>Método responsável por verificar se o tipo do valor sendo copiado é
     * um wrapper.</strong>
     *
     * @param clazz - {@linkplain Class}&lt;?&gt;
     * @return {@linkplain Boolean}
     */
    public static boolean isWrapperType(Class<?> clazz) {
        return WRAPPER_CACHE.computeIfAbsent(clazz, c ->
                WRAPPER_TYPES.contains(c) || WRAPPER_TYPES.stream().anyMatch(w -> w.isAssignableFrom(c))
        );
    }

    /**
     * Creates a set of all wrapper types.
     * <p>
     * This includes numbers, dates, text types, and other common wrapper types.
     *
     * @return A set of all wrapper types
     */
    public static Set<Class<?>> getWrapperTypes() {
        Set<Class<?>> wrappers = new HashSet<>();
        wrappers.add(Boolean.class);
        wrappers.add(Byte.class);
        wrappers.add(UUID.class);
        wrappers.addAll(numberTypes());
        wrappers.addAll(dateTypes());
        wrappers.addAll(textTypes());
        return wrappers;
    }

    /**
     * Creates a set of wrapper types for text classes.
     *
     * @return A set of text-related classes
     */
    public static Set<Class<?>> textTypes() {
        Set<Class<?>> aux = new HashSet<>();
        aux.add(String.class);
        aux.add(Character.class);
        aux.add(Format.class);
        return aux;
    }

    /**
     * Creates a set of wrapper types for date/time classes.
     *
     * @return A set of date/time-related classes
     */
    public static Set<Class<?>> dateTypes() {
        Set<Class<?>> aux = new HashSet<>();
        aux.add(Date.class);
        aux.add(Time.class);
        aux.add(LocalDateTime.class);
        aux.add(LocalDate.class);
        aux.add(LocalTime.class);
        aux.add(Temporal.class);
        aux.add(Instant.class);
        return aux;
    }

    /**
     * Creates a set of wrapper types for number classes.
     *
     * @return A set of number-related classes
     */
    public static Set<Class<?>> numberTypes() {
        Set<Class<?>> aux = new HashSet<>();
        aux.add(Integer.class);
        aux.add(Double.class);
        aux.add(Float.class);
        aux.add(Long.class);
        aux.add(Number.class);
        return aux;
    }

    /**
     * Creates a map of default values for primitive types.
     */
    private static void initDefaultValues() {
        DEFAULT_VALUES.put(boolean.class, Boolean.FALSE);
        DEFAULT_VALUES.put(byte.class, (byte) 0);
        DEFAULT_VALUES.put(short.class, (short) 0);
        DEFAULT_VALUES.put(int.class, 0);
        DEFAULT_VALUES.put(long.class, 0L);
        DEFAULT_VALUES.put(char.class, '\0');
        DEFAULT_VALUES.put(float.class, 0.0F);
        DEFAULT_VALUES.put(double.class, 0.0D);
    }

    @SuppressWarnings("unchecked")
    public static <T> T defaultValueFor(Class<T> clazz) {
        return (T) DEFAULT_VALUES.get(clazz);
    }
}
