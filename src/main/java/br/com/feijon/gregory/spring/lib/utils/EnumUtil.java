package br.com.feijon.gregory.spring.lib.utils;

import br.com.feijon.gregory.spring.lib.exception.ApiException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnumUtil {

    public static <T extends Enum<T>, R> Optional<T> getEnum(final Class<T> enumType, final Function<T, R> method, R expectedValue) {
        boolean hasNullValues = verifyNullValues(enumType, method, expectedValue);
        if (hasNullValues) {
            throw new ApiException("Please, define values for all arguments in getEnum method.");
        }
        return Stream.of(enumType.getEnumConstants()).filter(e -> {
            Object value = method.apply(e);
            return value != null && value.equals(expectedValue);
        }).findAny();
    }

    public static <T extends Enum<T>, R> T getEnumOrNull(final Class<T> enumType, final Function<T, R> method, R expectedValue) {
        boolean hasNullValues = verifyNullValues(enumType, method, expectedValue);
        if (hasNullValues) {
            return null;
        }
        Optional<T> opEnum = getEnum(enumType, method, expectedValue);
        return opEnum.orElse(null);
    }

    private static <T extends Enum<T>, R> boolean verifyNullValues(Class<T> enumType, Function<T,R> method, R expectedValue) {
        return (enumType == null || method == null || expectedValue == null);
    }
}
