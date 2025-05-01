package io.github.gregoryfeijon.utils;

import io.github.gregoryfeijon.exception.ApiException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.function.Supplier;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FieldUtil {

    public static <T> void setProtectedFieldValue(Field destField, T dest, Object sourceValue) {
        try {
            ReflectionUtil.setValueDynamicallyThroughSetterNameFromField(destField, dest, sourceValue);
        } catch (Exception e) {
            try {
                setFieldValueWithHandles(destField, dest, sourceValue);
            } catch (Exception ex) {
                try {
                    FieldUtils.writeField(dest, destField.getName(), sourceValue, true);
                } catch (IllegalAccessException exc) {
                    throw new ApiException("Erro ao definir valor para o campo " + destField.getName(), ex);
                }
            }
        }
    }

    private static <T> void setFieldValueWithHandles(Field field, T target, Object value) {
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(target.getClass(), MethodHandles.lookup());
            VarHandle varHandle = lookup.unreflectVarHandle(field);
            varHandle.set(target, value);
        } catch (Exception ex) {
            throw new ApiException("Erro ao definir valor via VarHandle para o campo: " + field.getName(), ex);
        }
    }

    public static Object getProtectedFieldValue(Field field, Object target) {
        try {
            return ReflectionUtil.getValueDynamicallyThroughGetterNameFromField(field, target);
        } catch (Exception e) {
            try {
                return getFieldValueWithHandles(field, target);
            } catch (Exception ex) {
                try {
                    return FieldUtils.readField(field, target, true);
                } catch (IllegalAccessException exc) {
                    throw new ApiException("Erro ao tentar obter valor do campo " + field.getName(), ex);
                }
            }
        }
    }

    private static Object getFieldValueWithHandles(Field field, Object target) {
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(target.getClass(), MethodHandles.lookup());
            VarHandle varHandle = lookup.unreflectVarHandle(field);
            return varHandle.get(target);
        } catch (Exception ex) {
            throw new ApiException("Erro ao obter valor do campo via VarHandle: " + field.getName(), ex);
        }
    }

    public static <T> boolean verifyNull(Supplier<T> getterValidate) {
        T value = getterValidate.get();
        return value == null;
    }
}