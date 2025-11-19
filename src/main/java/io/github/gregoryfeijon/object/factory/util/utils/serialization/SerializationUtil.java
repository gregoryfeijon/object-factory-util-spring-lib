package io.github.gregoryfeijon.object.factory.util.utils.serialization;

import io.github.gregoryfeijon.object.factory.util.exception.ApiException;
import io.github.gregoryfeijon.object.factory.util.utils.serialization.adapter.SerializerProvider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;

/**
 * Utility class for serialization and deserialization operations.
 * <p>
 * This class provides methods to serialize objects to byte arrays and deserialize them back,
 * with special handling for JSON serialization through the SerializerProvider.
 *
 * @author gregory.feijon
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SerializationUtil {

    private static final String DEFAULT_ERROR_MESSAGE = "Error while trying to serialize object!";

    /**
     * Deserializes a byte array into an object.
     *
     * @param bytes The byte array to deserialize
     * @return The deserialized object, or null if the input is null
     * @throws IllegalStateException If deserialization fails
     */
    @Nullable
    public static Object deserialize(@Nullable byte[] bytes) {
        if (bytes == null) {
            return null;
        } else {
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
                return ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new IllegalStateException(DEFAULT_ERROR_MESSAGE, e);
            }
        }
    }

    /**
     * Serializes an object to JSON and then to a ByteArrayOutputStream.
     *
     * @param <T> The type of the object to serialize
     * @param entity The object to serialize
     * @return A ByteArrayOutputStream containing the serialized object
     * @throws ApiException If serialization fails
     */
    public static <T> ByteArrayOutputStream serializaJsonDeUmObjeto(T entity) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(baos)) {
            out.writeObject(SerializerProvider.getAdapter().serialize(entity));
        } catch (IOException ex) {
            throw new ApiException(DEFAULT_ERROR_MESSAGE, ex);
        }
        return baos;
    }

    /**
     * Serializes a collection of objects to JSON and then to a ByteArrayOutputStream.
     *
     * @param <T> The type of objects in the collection
     * @param entities The collection of objects to serialize
     * @return A ByteArrayOutputStream containing the serialized collection
     * @throws ApiException If serialization fails
     */
    public static <T> ByteArrayOutputStream serializaJsonDeUmObjeto(Collection<T> entities) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(baos)) {
            out.writeObject(SerializerProvider.getAdapter().serialize(entities));
        } catch (IOException ex) {
            throw new ApiException(DEFAULT_ERROR_MESSAGE, ex);
        }
        return baos;
    }

    /**
     * Serializes an object directly to a ByteArrayOutputStream without JSON conversion.
     *
     * @param <T> The type of the object to serialize
     * @param entity The object to serialize
     * @return A ByteArrayOutputStream containing the serialized object
     * @throws ApiException If serialization fails
     */
    public static <T> ByteArrayOutputStream serializaObjeto(T entity) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(baos)) {
            out.writeObject(entity);
        } catch (IOException ex) {
            throw new ApiException(DEFAULT_ERROR_MESSAGE, ex);
        }
        return baos;
    }

    /**
     * Deserializes a byte array into an object.
     *
     * @param byteArr The byte array to deserialize
     * @return The deserialized object
     * @throws ApiException If deserialization fails
     */
    private static Object getObject(byte[] byteArr) {
        InputStream input = new ByteArrayInputStream(byteArr);
        Object retorno;
        try (ObjectInputStream in = new ObjectInputStream(input)) {
            retorno = in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            throw new ApiException(DEFAULT_ERROR_MESSAGE, ex);
        }
        return retorno;
    }

    /**
     * Serializes an object to JSON and returns the result as a byte array.
     *
     * @param <T> The type of the object to serialize
     * @param entity The object to serialize
     * @return A byte array containing the serialized object
     */
    public static <T> byte[] serializaJsonDeUmObjetoGetAsByte(T entity) {
        return serializaJsonDeUmObjeto(entity).toByteArray();
    }

    /**
     * Serializes a collection of objects to JSON and returns the result as a byte array.
     *
     * @param <T> The type of objects in the collection
     * @param entities The collection of objects to serialize
     * @return A byte array containing the serialized collection
     */
    public static <T> byte[] serializaJsonDeUmObjetoGetAsByte(Collection<T> entities) {
        return serializaJsonDeUmObjeto(entities).toByteArray();
    }

    /**
     * Serializes an object directly and returns the result as a byte array.
     *
     * @param <T> The type of the object to serialize
     * @param entity The object to serialize
     * @return A byte array containing the serialized object
     */
    public static <T> byte[] serializaObjetoGetAsByte(T entity) {
        return SerializationUtil.serializaObjeto(entity).toByteArray();
    }

    /**
     * Deserializes a byte array into an object.
     *
     * @param serializedObjects The byte array to deserialize
     * @return The deserialized object
     */
    public static Object getDesserealizedObject(byte[] serializedObjects) {
        return getObject(serializedObjects);
    }

    /**
     * Deserializes a byte array into an object and returns its string representation.
     *
     * @param serializedObjects The byte array to deserialize
     * @return The string representation of the deserialized object
     */
    public static String getDesserealizedObjectAsString(byte[] serializedObjects) {
        return getObject(serializedObjects).toString();
    }
}