package br.com.feijon.gregory.spring.lib.utils.serialization;

import br.com.feijon.gregory.spring.lib.exception.ApiException;
import br.com.feijon.gregory.spring.lib.utils.serialization.adapter.SerializerProvider;
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

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SerializationUtil {

    private static final String DEFAULT_ERROR_MESSAGE = "Error while trying to serialize object!";

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

    public static <T> ByteArrayOutputStream serializaJsonDeUmObjeto(T entity) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(baos)) {
            out.writeObject(SerializerProvider.getAdapter().serialize(entity));
        } catch (IOException ex) {
            throw new ApiException(DEFAULT_ERROR_MESSAGE, ex);
        }
        return baos;
    }

    public static <T> ByteArrayOutputStream serializaJsonDeUmObjeto(Collection<T> entities) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(baos)) {
            out.writeObject(SerializerProvider.getAdapter().serialize(entities));
        } catch (IOException ex) {
            throw new ApiException(DEFAULT_ERROR_MESSAGE, ex);
        }
        return baos;
    }

    public static <T> ByteArrayOutputStream serializaObjeto(T entity) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(baos)) {
            out.writeObject(entity);
        } catch (IOException ex) {
            throw new ApiException(DEFAULT_ERROR_MESSAGE, ex);
        }
        return baos;
    }

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

    public static <T> byte[] serializaJsonDeUmObjetoGetAsByte(T entity) {
        return serializaJsonDeUmObjeto(entity).toByteArray();
    }

    public static <T> byte[] serializaJsonDeUmObjetoGetAsByte(Collection<T> entities) {
        return serializaJsonDeUmObjeto(entities).toByteArray();
    }

    public static <T> byte[] serializaObjetoGetAsByte(T entity) {
        return SerializationUtil.serializaObjeto(entity).toByteArray();
    }

    public static Object getDesserealizedObject(byte[] serializedObjects) {
        return getObject(serializedObjects);
    }

    public static String getDesserealizedObjectAsString(byte[] serializedObjects) {
        return getObject(serializedObjects).toString();
    }
}
