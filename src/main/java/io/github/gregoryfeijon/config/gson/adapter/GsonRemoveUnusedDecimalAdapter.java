package io.github.gregoryfeijon.config.gson.adapter;

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

/**
 * A Gson type adapter that removes unnecessary decimal places when serializing number types.
 * <p>
 * This adapter ensures that numbers without decimal parts (e.g., 10.0) are serialized as integers (10),
 * while preserving decimal places when they are actually used (e.g., 10.5).
 *
 * @param <T> The number type to adapt
 * @author gregory.feijon
 */
public class GsonRemoveUnusedDecimalAdapter<T> extends TypeAdapter<T> {

    private final Class<T> numberClass;

    /**
     * Static mapping of number classes to their respective conversion functions.
     * This map provides a clean way to convert BigDecimal to specific number types.
     */
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

    /**
     * Constructs a new adapter for the specified number type.
     *
     * @param type The TypeToken representing the number type
     */
    public GsonRemoveUnusedDecimalAdapter(TypeToken<T> type) {
        this.numberClass = (Class<T>) type.getRawType();
    }

    /**
     * Writes a number value as JSON, removing unnecessary decimal places.
     * <p>
     * If the number has no decimal part (e.g., 10.0), it will be written as an integer (10).
     *
     * @param jsonWriter The JSON writer
     * @param value The number value to write
     * @throws IOException If an I/O error occurs
     */
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

    /**
     * Reads a JSON number value and converts it to the appropriate number type.
     *
     * @param jsonReader The JSON reader
     * @return The number value of the appropriate type
     * @throws IOException If an I/O error occurs or if the token is not a number
     */
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

    /**
     * Converts a value to a BigDecimal.
     *
     * @param value The number value to convert
     * @return The BigDecimal representation of the value
     */
    private BigDecimal getBigDecimalValue(T value) {
        if (value.getClass().isInstance(BigDecimal.class)) {
            return (BigDecimal) value;
        } else {
            return new BigDecimal(value.toString());
        }
    }

    /**
     * Creates a number of the appropriate type from a BigDecimal value.
     *
     * @param value The BigDecimal value to convert
     * @return The number of the appropriate type
     * @throws IllegalArgumentException If the conversion is not supported
     */
    private T createNumber(BigDecimal value) {
        Function<BigDecimal, ? extends Number> conversionFunction = CLASS_VALUE_FUNCTION_MAP.get(numberClass);
        if (conversionFunction != null) {
            return numberClass.cast(conversionFunction.apply(value));
        }
        throw new IllegalArgumentException("Error when trying to convert number between types");
    }
}