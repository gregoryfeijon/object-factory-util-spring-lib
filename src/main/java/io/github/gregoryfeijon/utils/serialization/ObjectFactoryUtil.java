package io.github.gregoryfeijon.utils.serialization;


import com.google.gson.Gson;
import io.github.gregoryfeijon.domain.annotation.FieldCopyName;
import io.github.gregoryfeijon.domain.annotation.ObjectConstructor;
import io.github.gregoryfeijon.domain.annotation.ObjectCopyExclude;
import io.github.gregoryfeijon.exception.ApiException;
import io.github.gregoryfeijon.utils.FieldUtil;
import io.github.gregoryfeijon.utils.ReflectionTypeUtils;
import io.github.gregoryfeijon.utils.ReflectionUtil;
import io.github.gregoryfeijon.utils.gson.GsonTypesUtil;
import io.github.gregoryfeijon.utils.serialization.adapter.SerializerAdapter;
import io.github.gregoryfeijon.utils.serialization.adapter.SerializerProvider;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.gregoryfeijon.utils.ReflectionTypeUtils.defaultValueFor;
import static io.github.gregoryfeijon.utils.ReflectionTypeUtils.getRawType;
import static io.github.gregoryfeijon.utils.ReflectionTypeUtils.isClassMapCollection;
import static io.github.gregoryfeijon.utils.ReflectionTypeUtils.isCollection;
import static io.github.gregoryfeijon.utils.ReflectionTypeUtils.isPrimitiveOrEnum;
import static io.github.gregoryfeijon.utils.ReflectionTypeUtils.isWrapperType;
import static java.util.Arrays.stream;

/**
 * Utility class for creating deep copies of objects.
 * <p>
 * This class provides methods to create copies of objects, including complex objects
 * with nested structures, collections, and primitive types. It uses a combination of
 * reflection, serialization, and direct field copying to achieve deep copying.
 *
 * @author gregory.feijon
 */
@SuppressWarnings("java:S6204")
//warning do .toList() suprimida, uma vez que não se aplica nessa classe, que é uma classe útil
@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ObjectFactoryUtil {

    private static final SerializerAdapter SERIALIZER;
    private static final Predicate<Field> PREDICATE_MODIFIERS;

    static {
        SERIALIZER = SerializerProvider.getAdapter();
        PREDICATE_MODIFIERS = predicateModifiers();
    }

    /**
     * Creates deep copies of all objects in a collection.
     * <p>
     * This method creates a new list containing deep copies of all objects in the provided collection.
     *
     * @param <T>            The type of objects in the collection
     * @param entitiesToCopy The collection of objects to copy
     * @return A list containing deep copies of the original objects
     * @throws ApiException If the collection is empty or if an error occurs during copying
     */
    public static <T> List<T> copyAllObjectsFromCollection(Collection<T> entitiesToCopy) {
        verifyCollection(entitiesToCopy);
        return entitiesToCopy.stream().map(createCopy()).collect(Collectors.toList());
    }

    /**
     * <strong>Método que retorna uma cópia de uma lista de objetos.</strong>
     *
     * <p>
     * O Tipo da coleção retornada não precisa ser igual ao tipo da coleção copiada,
     * basta que os objetos possuam atributos com o mesmo nome, que os valores
     * desses atributos serão copiados.
     * <p>
     *
     * @param <T>            - define o tipo da collection resultante
     * @param entitiesToCopy - {@linkplain Collection}&lt;?&gt;
     * @param returnType     - {@linkplain Class}&lt;T&gt;
     * @return {@linkplain List}&lt;T&gt;
     */
    public static <T> List<T> copyAllObjectsFromCollection(Collection<?> entitiesToCopy, Class<T> returnType) {
        verifyCollection(entitiesToCopy);
        return entitiesToCopy.stream().map(createCopy(returnType)).collect(Collectors.toList());
    }

    /**
     * <strong> Método para copiar todos os elementos de uma {@linkplain Collection
     * coleção} e retornar em um tipo escolhido de {@linkplain Collection
     * coleção}.</strong>
     *
     * @param <T>            - define o tipo da collection copiada
     * @param <U>            - define o tipo da collection resultante, gerada através do supplier
     * @param entitiesToCopy - {@linkplain Collection}&lt;T&gt;
     * @param supplier       - {@linkplain Supplier}&lt;U&gt;
     * @return U
     */
    public static <T, U extends Collection<T>> U copyAllObjectsFromCollection(Collection<T> entitiesToCopy,
                                                                              Supplier<U> supplier) {
        verifyCollectionAndSupplier(entitiesToCopy, supplier);
        return entitiesToCopy.stream().map(createCopy()).collect(Collectors.toCollection(supplier));
    }

    /**
     * Creates deep copies of all objects in a collection, converting them to a different type.
     * <p>
     * This method creates a new collection containing deep copies of all objects in the
     * provided collection, converting each object to the specified target type.
     *
     * @param <T>            The target type
     * @param <S>            The source type
     * @param <U>            The type of the resulting collection
     * @param entitiesToCopy The collection of objects to copy
     * @param returnType     The class of the target type
     * @param supplier       A supplier that creates the target collection
     * @return A collection of the specified type containing deep copies of the original objects
     * @throws ApiException If the collection is empty or if an error occurs during copying
     */
    public static <T, S, U extends Collection<T>> U copyAllObjectsFromCollection(Collection<S> entitiesToCopy,
                                                                                 Supplier<U> supplier, Class<T> returnType) {
        verifyCollectionAndSupplier(entitiesToCopy, supplier);
        return entitiesToCopy.stream().map(createCopy(returnType)).collect(Collectors.toCollection(supplier));
    }

    /**
     * Verifies if a collection and a supplier are both non-null.
     *
     * @param <T>            The type of objects in the collection
     * @param <U>            The type of collection to be created by the supplier
     * @param entitiesToCopy The collection to verify
     * @param supplier       The supplier to verify
     * @throws ApiException If either the collection or supplier is null
     */
    private static <T, U> void verifyCollectionAndSupplier(Collection<T> entitiesToCopy, Supplier<U> supplier) {
        verifyCollection(entitiesToCopy);
        if (supplier == null) {
            throw new ApiException("O tipo de coleção especificada para retorno é nulo.");
        }
    }

    /**
     * Verifies that a collection is not empty.
     *
     * @param <T>            The type of objects in the collection
     * @param entitiesToCopy The collection to verify
     * @throws ApiException If the collection is empty
     */
    private static <T> void verifyCollection(Collection<T> entitiesToCopy) {
        if (CollectionUtils.isEmpty(entitiesToCopy)) {
            throw new ApiException("A lista a ser copiada não possui elementos.");
        }
    }

    /**
     * Creates a function that produces a deep copy of an object.
     *
     * @param <T> The type of objects to copy
     * @return A function that creates deep copies
     */
    private static <T> Function<T, T> createCopy() {
        return ObjectFactoryUtil::createFromObject;
    }

    /**
     * Creates a function that produces a deep copy of an object, converting it to a different type.
     *
     * @param <T>        The target type
     * @param <S>        The source type
     * @param returnType The class of the target type
     * @return A function that creates deep copies with type conversion
     */
    private static <T, S> Function<S, T> createCopy(Class<T> returnType) {
        return i -> createFromObject(i, returnType);
    }

    /**
     * Creates a deep copy of an object, converting it to a different type.
     * <p>
     * This method creates a new instance of the target type and copies
     * all matching fields from the source object to the new instance.
     *
     * @param <T>        The target type
     * @param <S>        The source type
     * @param source     The source object to copy
     * @param returnType The class of the target type
     * @return A new instance of the target type with copied fields
     * @throws ApiException If the source object is null or if an error occurs during copying
     */
    public static <T, S> T createFromObject(S source, Class<T> returnType) {
        verifySourceObject(source);
        T dest = BeanUtils.instantiateClass(returnType);
        createFromObject(source, dest);
        return dest;
    }

    /**
     * Creates a deep copy of an object of the same type.
     * <p>
     * This method creates a new instance of the same class as the source object
     * and copies all fields from the source to the new instance.
     *
     * @param <T>    The type of the object
     * @param source The object to copy
     * @return A deep copy of the source object
     * @throws ApiException If the source object is null or if an error occurs during copying
     */
    @SuppressWarnings("unchecked")
    public static <T> T createFromObject(T source) {
        verifySourceObject(source);
        Object dest = BeanUtils.instantiateClass(source.getClass());
        createFromObject(source, dest);
        return (T) dest;
    }

    /**
     * Copies all fields from a source object to a destination object.
     * <p>
     * This method is the core implementation of the object copying functionality.
     * It handles different types of fields, including primitive types, collections,
     * and nested objects.
     *
     * @param <S>    The source type
     * @param <T>    The destination type
     * @param source The source object
     * @param dest   The destination object
     * @throws ApiException If an error occurs during copying
     */
    public static <T, S> void createFromObject(S source, T dest) {
        verifySourceAndDestObjects(source, dest);
        var sourceDestFieldsMap = createSourceDestFieldMaps(source, dest);
        sourceDestFieldsMap.entrySet().parallelStream().forEach(fieldsEntry -> {
            Object sourceValue = verifyValue(fieldsEntry.getKey(), fieldsEntry.getValue(), source);
            FieldUtil.setProtectedFieldValue(fieldsEntry.getValue(), dest, sourceValue);
        });
    }

    private static <S, T> Map<Field, Field> createSourceDestFieldMaps(S source, T dest) {
        var sourceFields = getFieldsToCopy(source, dest);
        var destFields = ReflectionUtil.getFieldsAsCollection(dest, ArrayList::new);

        if (sourceFields.isEmpty() || destFields.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Field> sourceFieldMap = buildFieldKeyMap(sourceFields);
        Map<String, Field> destFieldMap = buildFieldKeyMap(destFields);

        return sourceFieldMap.entrySet().stream()
                .filter(entry -> destFieldMap.containsKey(entry.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getValue,
                        entry -> destFieldMap.get(entry.getKey())
                ));
    }


    private static Map<String, Field> buildFieldKeyMap(List<Field> fields) {
        return fields.stream().collect(Collectors.toMap(
                ObjectFactoryUtil::resolveFieldKey,
                Function.identity(),
                (a, b) -> {
                    log.warn("Duplicate field key '{}' detected. Keeping first occurrence.", a.getName());
                    return a;
                }
        ));
    }

    private static String resolveFieldKey(Field field) {
        FieldCopyName ann = field.getAnnotation(FieldCopyName.class);
        if (ann != null && StringUtils.hasText(ann.value())) {
            return ann.value().toLowerCase();
        }
        return field.getName().toLowerCase();
    }

    /**
     * <strong>Método para verificar se os objetos de origem e destino são
     * válidos.</strong>
     *
     * @param <T>    - define o tipo do objeto retornado
     * @param <S>    - define o tipo do objeto copiado
     * @param source &lt;S&gt;
     * @param dest   &lt;T&gt;
     */
    private static <T, S> void verifySourceAndDestObjects(S source, T dest) {
        verifySourceObject(source);
        if (dest == null) {
            throw new ApiException("O objeto de destino é nulo.");
        }
    }

    /**
     * <strong> Método para verificar se o objeto a ser copiado é válido.</strong>
     *
     * @param <S>    - define o tipo do objeto copiado
     * @param source &lt;S&gt;
     */
    private static <S> void verifySourceObject(S source) {
        if (source == null) {
            throw new ApiException("O objeto a ser copiado é nulo.");
        }
    }

    /**
     * <strong>Method that retrieves all fields that should be copied from the
     * source object.</strong>
     *
     * <p>
     * First, it uses the {@linkplain ReflectionUtil#getFieldsAsCollection(Object)
     * getFieldsAsCollection} method to obtain all fields from the source data object.
     * From this list, another list of fields to be removed is created by checking
     * final fields, which will not be processed. Subsequently, the destination object
     * is verified and, if there are fields defined in the
     * {@linkplain ObjectConstructor#exclude() exclude} of the
     * {@linkplain ObjectConstructor} annotation, they will also be added to the
     * exclusion list. Fields annotated with {@linkplain ObjectCopyExclude} in both
     * source and dest are also excluded.
     * The separated fields are removed, returning the list with the remaining ones.
     * <p>
     *
     * @param <T>    - defines the type of the returned object
     * @param <S>    - defines the type of the copied object
     * @param source - &lt;S&gt;
     * @param dest   - &lt;T&gt;
     * @return {@linkplain List}&lt;{@linkplain Field}&gt;
     */
    private static <T, S> List<Field> getFieldsToCopy(S source, T dest) {
        List<Field> sourceFields = new ArrayList<>(ReflectionUtil.getFieldsAsCollection(source));
        Set<Field> fieldsToRemove = sourceFields.stream()
                .filter(PREDICATE_MODIFIERS)
                .collect(Collectors.toSet());

        String[] exclude = getExcludeFromObjectConstructorAnnotation(dest);
        if (ArrayUtils.isNotEmpty(exclude)) {
            getFieldsListedToExclude(fieldsToRemove, sourceFields, exclude);
        }
        getFieldsFromSourceAnnotatedToExclude(fieldsToRemove, sourceFields);
        getFieldsFromDestAnnotatedToExclude(fieldsToRemove, sourceFields, dest);

        if (!fieldsToRemove.isEmpty()) {
            sourceFields.removeAll(fieldsToRemove);
        }

        return sourceFields;
    }

    private static void getFieldsFromSourceAnnotatedToExclude(Set<Field> fieldsToRemove, List<Field> sourceFields) {
        sourceFields.stream()
                .filter(f -> f.isAnnotationPresent(ObjectCopyExclude.class))
                .forEach(fieldsToRemove::add);
    }

    /**
     * <strong>Method that adds to the exclusion list the fields from source that have
     * corresponding fields in dest annotated with {@linkplain ObjectCopyExclude}.</strong>
     *
     * <p>
     * This method searches the destination object for fields annotated with @ObjectCopyExclude,
     * and for each one found, it looks in the source for a field with the same name to add
     * to the exclusion list. The Set ensures there are no duplications.
     * </p>
     *
     * @param <T>            - defines the type of the destination object
     * @param fieldsToRemove {@linkplain Set}&lt;{@linkplain Field}&gt; - set of fields to be removed
     * @param sourceFields   {@linkplain List}&lt;{@linkplain Field}&gt; - list of fields from source
     * @param dest           &lt;T&gt; - destination object
     */
    private static <T> void getFieldsFromDestAnnotatedToExclude(Set<Field> fieldsToRemove,
                                                                List<Field> sourceFields,
                                                                T dest) {
        List<Field> destFields = ReflectionUtil.getFieldsAsCollection(dest, ArrayList::new);

        destFields.stream()
                .filter(f -> f.isAnnotationPresent(ObjectCopyExclude.class))
                .forEach(destField -> sourceFields.stream()
                        .filter(sourceField -> sourceField.getName().equalsIgnoreCase(destField.getName()))
                        .findAny()
                        .ifPresent(fieldsToRemove::add));
    }

    /**
     * <strong>Method that adds the fields specified in the annotation defined in the destination
     * class type of the copied data.</strong>
     *
     * @param fieldsToRemove {@linkplain Set}&lt;{@linkplain Field}&gt;
     * @param sourceFields   {@linkplain List}&lt;{@linkplain Field}&gt;
     * @param exclude        {@linkplain String}[]
     */
    private static void getFieldsListedToExclude(Set<Field> fieldsToRemove, List<Field> sourceFields, String[] exclude) {
        stream(exclude)
                .forEach(excludeField -> sourceFields.stream()
                        .filter(sourceField -> sourceField.getName().equalsIgnoreCase(excludeField))
                        .findAny()
                        .ifPresent(fieldsToRemove::add));
    }

    /**
     * <strong>Method that verifies the destination Object. If the
     * {@linkplain ObjectConstructor} annotation is present in the class, returns the exclude.
     * Otherwise, returns an empty array.</strong>
     *
     * @param <T>  - method type definer
     * @param dest - &lt;T&gt;
     * @return {@linkplain String}[]
     */
    private static <T> String[] getExcludeFromObjectConstructorAnnotation(T dest) {
        if (dest.getClass().isAnnotationPresent(ObjectConstructor.class)) {
            return dest.getClass().getAnnotation(ObjectConstructor.class).exclude();
        }
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }

    /**
     * <strong>Método para verificar os casos especiais em que os tipos do objeto de
     * origem e destino são diferentes e é necessário um tratamento específico para
     * retornar o valor correto.</strong>
     *
     * <p>
     * Faz tratamento específicos entre Wrappers e tipos primitivos, tanto do
     * atributo copiado, quanto do destino. Também possui um tratamento específico
     * no caso do atributo do objeto copiado ser uma {@linkplain String} e o
     * atributo do destino ser um {@linkplain Enum}. No caso de
     * {@linkplain Collection} ou {@linkplain Map}, apenas retorna null, pois é um
     * tratamento mais específico de implementação.
     * <p>
     *
     * @param <S>         - define o tipo do objeto copiado
     * @param sourceField - {@linkplain Field}
     * @param destField   - {@linkplain Field}
     * @param source      - &lt;S&gt;
     * @return {@linkplain Object}
     */
    private static <S> Object verifyValue(Field sourceField, Field destField, S source) {
        Object sourceValue = FieldUtil.getProtectedFieldValue(sourceField, source);
        sourceValue = unproxyValueIfNeeded(sourceValue);
        Class<?> sourceFieldType = sourceField.getType();
        Class<?> destFieldType = destField.getType();

        if (sourceFieldType == destFieldType) {
            return copyValue(sourceField, destField, sourceValue);
        }

        if (isWrapperType(sourceFieldType) && destFieldType.isPrimitive() && sourceValue == null) {
            return defaultValueFor(destFieldType);
        }

        if (isWrapperType(destFieldType) && sourceFieldType.isPrimitive() && Objects.equals(sourceValue, defaultValueFor(sourceFieldType))) {
            return null;
        }

        if (sourceFieldType.isEnum() || destFieldType.isEnum()) {
            return validateEnums(sourceField, destField, sourceValue);
        }

        if (isClassMapCollection(destFieldType) || isClassMapCollection(sourceFieldType)) {
            return null;
        }

        return copyValue(sourceField, destField, sourceValue);
    }

    private static Object unproxyValueIfNeeded(Object value) {
        if (value instanceof HibernateProxy proxy) {
            LazyInitializer li = proxy.getHibernateLazyInitializer();
            if (li.isUninitialized()) {
                return BeanUtils.instantiateClass(li.getPersistentClass());
            }
            return li.getImplementation();
        }
        if (value instanceof Collection<?> collection) {
            return collection.stream()
                    .map(ObjectFactoryUtil::unproxyValueIfNeeded)
                    .collect(Collectors.toList());
        }
        if (value instanceof Map<?, ?> map) {
            return map.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> unproxyValueIfNeeded(e.getKey()),
                            e -> unproxyValueIfNeeded(e.getValue())
                    ));
        }
        return value;
    }

    /**
     * <strong>Método validação de enum, para o caso de algum dos valores dos atributos envolvidos
     * na cópia seja do tipo enum</strong>
     *
     * @param sourceField - {@linkplain Field}
     * @param destField   - {@linkplain Field}
     * @param sourceValue - {@linkplain Object}
     * @return {@linkplain Object}
     */
    private static Object validateEnums(Field sourceField, Field destField, Object sourceValue) {
        Class<?> sourceFieldType = sourceField.getType();
        Class<?> destFieldType = destField.getType();

        if (destFieldType.isEnum()) {
            if (sourceFieldType.equals(String.class)) {
                return findEnumConstantEquivalent(destFieldType, sourceValue);
            } else if (sourceFieldType.isEnum() && sourceValue != null) {
                return findEnumConstantEquivalent(destFieldType, sourceValue.toString());
            }
        }
        if (sourceFieldType.isEnum() && (sourceValue != null && destFieldType.equals(String.class))) {
            return sourceValue.toString();
        }
        return null;
    }

    /**
     * <strong> Método para encontrar a constante enum equivalente à String que está
     * sendo copiada.</strong>
     *
     * @param type        - {@linkplain Class}&lt;?&gt;
     * @param sourceValue {@linkplain Object}
     * @return {@linkplain Object}
     */
    private static Object findEnumConstantEquivalent(Class<?> type, Object sourceValue) {
        return Stream.of(type.getEnumConstants())
                .filter(enumConstant -> Objects.equals(enumConstant.toString(), sourceValue))
                .findFirst()
                .orElse(null);
    }

    /**
     * <strong>Método para verificar o tipo do valor copiado, com o intuito de
     * definir a melhor forma para copiá-lo.</strong>
     *
     * <p>
     * Verifica se o {@linkplain Field} é um tipo primitivo ou {@linkplain Enum} e,
     * caso seja, apenas obtem o valor do campo de nome correspondente.
     * Posteriormente, verifica se é um Wrapper, que será copiado apenas via
     * serialização. Caso o valor seja uma {@linkplain Collection} ou um
     * {@linkplain Map}, também possui um fluxo para validação dos tipos e devida
     * cópia dos valores. Se não for nenhum desses tipos, é necessário utilizar o
     * método {@linkplain ObjectFactoryUtil#serializingCloneObjects(Object, Class) objectCopy}, que cria
     * uma nova instância do objeto e faz a cópia via serialização, para garantir
     * que seja feita a cópia por valor, não por referência.
     * <p>
     *
     * @param sourceField - {@linkplain Field}
     * @param destField   - {@linkplain Field}
     * @param sourceValue - {@linkplain Object}
     * @return {@linkplain Object}
     */
    private static Object copyValue(Field sourceField, Field destField, Object sourceValue) {
        Class<?> sourceFieldType = sourceField.getType();
        Class<?> destFieldType = destField.getType();
        if (isPrimitiveOrEnum(sourceFieldType)) {
            return sourceValue;
        }
        if (isWrapperType(sourceFieldType)) {
            return serializingClone(sourceValue, destFieldType);
        }
        if (isClassMapCollection(sourceField.getType())) {
            return serializingCloneCollectionMap(sourceValue, destField.getGenericType());
        }
        try {
            return serializingCloneObjects(sourceValue, destFieldType);
        } catch (Exception ex) {
            throw new ApiException(ex.getMessage());
        }
    }

    /**
     * <strong>Método para copiar o valor de objetos do tipo
     * <i>Wrappers</i>.</strong>
     *
     * @param sourceValue - {@linkplain Object}
     * @param clazz       - {@linkplain Class}&lt;?&gt;
     * @return {@linkplain Object}
     */
    private static Object serializingClone(Object sourceValue, Class<?> clazz) {
        if (sourceValue != null) {
            return serializingCloneObjects(sourceValue, clazz);
        }
        return null;
    }

    /**
     * <strong>Método que efetivamente faz a cópia dos valores via serialização, nos
     * casos de <i>Wrappers</i> e objetos.</strong>
     *
     * @param sourceValue - {@linkplain Object}
     * @param clazz       - {@linkplain Class}&lt;?&gt;
     * @return {@linkplain Object}
     */
    private static Object serializingCloneObjects(Object sourceValue, Class<?> clazz) {
        Object clone;
        byte[] byteClone;
        if (ReflectionTypeUtils.isSimpleType(clazz)) {
            byteClone = SerializationUtils.serialize(sourceValue);
            clone = SerializationUtil.deserialize(byteClone);
        } else {
            byteClone = SerializationUtil.serializaJsonDeUmObjetoGetAsByte(sourceValue);
            clone = SERIALIZER.deserialize(SerializationUtil.getDesserealizedObjectAsString(byteClone), clazz);
        }
        return clone;
    }

    /**
     * <strong>Método que efetivamente faz a cópia do valor via serialização para
     * {@linkplain Collection} e {@linkplain Map}.</strong>
     *
     * @param sourceValue - {@linkplain Object}
     * @param genericType - {@linkplain Type}
     * @return {@linkplain Object}
     */
    private static Object serializingCloneCollectionMap(Object sourceValue, Type genericType) {
        Object clone = null;
        if (sourceValue != null) {
            try {
                byte[] byteClone = SerializationUtil.serializaJsonDeUmObjetoGetAsByte(sourceValue);
                if (isCollection(sourceValue.getClass())) {
                    clone = verifyList(sourceValue, genericType, byteClone);
                } else {
                    clone = SERIALIZER.deserialize(SerializationUtil.getDesserealizedObjectAsString(byteClone), genericType);
                }
            } catch (ClassNotFoundException ex) {
                throw new ApiException("Erro ao deserializar collection na cópia de objeto.", ex);
            }
        }
        return clone;
    }

    /**
     * <strong>Método que executa a verificação da lista, para poder definir os
     * tipos corretamente para a cópia do valor via serialização usando o
     * {@linkplain Gson}.</strong>
     *
     * <p>
     * Primeiramente passa por uma verificação do tipo e, caso passe, executa a
     * desserialização usando o {@linkplain Type genericType} normalmente. Caso
     * capture alguma exception no processo, significa que possui algum tipo
     * genérico. Nesse caso, passará por um processo para identificar o tipo
     * utilizado em <i>Runtime</i> através do valor, para que possa ser feita a
     * desserialização da forma devida.
     * <p>
     *
     * @param sourceValue - {@linkplain Object}
     * @param genericType - {@linkplain Type}
     * @param byteClone   - byte[]
     * @return {@linkplain Object}
     * @throws ClassNotFoundException - exception lançada para testar se o tipo existe
     */
    @SuppressWarnings("unchecked")
    private static Object verifyList(Object sourceValue, Type genericType, byte[] byteClone) throws ClassNotFoundException {
        Object clone = null;
        try {
            verifyType(genericType);
            clone = desserializeCollection(byteClone, genericType);
        } catch (BeanInstantiationException | ClassNotFoundException ex) {
            List<Object> aux = new ArrayList<>(Collections.checkedCollection((Collection<Object>) sourceValue, Object.class));
            if (!CollectionUtils.isEmpty(aux)) {
                Class<?> objectType = aux.getFirst().getClass();
                clone = desserializeCollection(byteClone, GsonTypesUtil.getType(getRawType(genericType), objectType));
            }
        }
        return clone;
    }

    /**
     * <strong>Método para verificar o tipo dos parâmetros da
     * {@linkplain Collection}, de modo a conseguir definir os tipos utilizados na
     * desserialização.</strong>
     *
     * <p>
     * Primeiramente passa por uma verificação dos parâmetros da
     * {@linkplain Collection}, que identifica se o valor é um tipo primitivo,
     * {@linkplain Enum} ou <i>Wrapper</i>. Caso não seja, tenta instanciar através
     * do seu tipo.
     * <p>
     *
     * @param genericType - {@linkplain Type}
     * @throws ClassNotFoundException     - exception lançada para testar se o tipo existe
     * @throws BeanInstantiationException - exception lançada para testar se o tipo é instanciável
     */
    private static void verifyType(Type genericType) throws ClassNotFoundException, BeanInstantiationException {
        ParameterizedType typeTest = (ParameterizedType) genericType;
        for (Type type : typeTest.getActualTypeArguments()) {
            Class<?> clazz = ClassUtils.getClass(type.getTypeName());
            if (!isPrimitiveOrEnum(clazz) && !isWrapperType(clazz)) {
                BeanUtils.instantiateClass(clazz);
            }
        }
    }

    /**
     * <strong>Método para desserialização, usando as classes utils
     * {@linkplain SerializerProvider} e {@linkplain SerializationUtil}.</strong>
     *
     * @param byteClone   - byte[]
     * @param genericType - {@linkplain Type}
     * @return {@linkplain Object}
     */
    private static Object desserializeCollection(byte[] byteClone, Type genericType) {
        return SERIALIZER.deserialize(SerializationUtil.getDesserealizedObjectAsString(byteClone), genericType);
    }

    /**
     * Creates a predicate that identifies constant fields.
     * <p>
     * The predicate returns true for fields that are both static and final.
     *
     * @return A predicate for identifying constant fields
     */
    private static Predicate<Field> predicateModifiers() {
        return p -> Modifier.isStatic(p.getModifiers()) && Modifier.isFinal(p.getModifiers());
    }
}