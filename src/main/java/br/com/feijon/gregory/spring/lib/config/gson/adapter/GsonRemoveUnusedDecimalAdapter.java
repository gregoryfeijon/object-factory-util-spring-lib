package br.com.feijon.gregory.spring.lib.config.gson.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class GsonRemoveUnusedDecimalAdapter<T> extends TypeAdapter<T> {

    private final Class<T> numberClass;
    private static final Map<Class<?>, Function<BigDecimal, ? extends Number>> CLASS_VALUE_FUNCTION_MAP = new HashMap<>();

    static {
        CLASS_VALUE_FUNCTION_MAP.put(Byte.class, BigDecimal::byteValue);
        CLASS_VALUE_FUNCTION_MAP.put(Short.class, BigDecimal::shortValue);
        CLASS_VALUE_FUNCTION_MAP.put(Integer.class, BigDecimal::intValue);
        CLASS_VALUE_FUNCTION_MAP.put(Long.class, BigDecimal::longValue);
        CLASS_VALUE_FUNCTION_MAP.put(Float.class, BigDecimal::floatValue);
        CLASS_VALUE_FUNCTION_MAP.put(Double.class, BigDecimal::doubleValue);
        CLASS_VALUE_FUNCTION_MAP.put(BigDecimal.class, Function.identity());
    }

    public GsonRemoveUnusedDecimalAdapter(TypeToken<T> type) {
        this.numberClass = (Class<T>) type.getRawType();
    }

    @Override
    public void write(JsonWriter jsonWriter, T value) throws IOException {
        if (value == null) {
            jsonWriter.nullValue();
            return;
        }
        if (Number.class.isAssignableFrom(numberClass)) {
            BigDecimal decimalValue = getBigDecimalValue(value);
            long longValue = decimalValue.longValue();
            if (decimalValue.doubleValue() == longValue) {
                jsonWriter.value(longValue);
            } else {
                jsonWriter.value(decimalValue);
            }
        }
    }

    @Override
    public T read(JsonReader jsonReader) throws IOException {
        JsonToken token = jsonReader.peek();
        if (token == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        } else if (token == JsonToken.NUMBER) {
            String value = jsonReader.nextString();
            return createNumber(new BigDecimal(value));
        }
        throw new IOException("Expected a number but found: " + token);
    }

    private BigDecimal getBigDecimalValue(T value) {
        if (value.getClass().isInstance(BigDecimal.class)) {
            return (BigDecimal) value;
        } else {
            return new BigDecimal(value.toString());
        }
    }

    private T createNumber(BigDecimal value) {
        Function<BigDecimal, ? extends Number> conversionFunction = CLASS_VALUE_FUNCTION_MAP.get(numberClass);
        if (conversionFunction != null) {
            return numberClass.cast(conversionFunction.apply(value));
        }
        throw new IllegalArgumentException("Error when trying to convert number between types");
    }
}

