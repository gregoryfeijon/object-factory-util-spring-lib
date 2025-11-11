package io.github.gregoryfeijon.config.gson.strategy;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import io.github.gregoryfeijon.domain.annotation.JsonExclude;
import org.springframework.stereotype.Component;

/**
 * An exclusion strategy for Gson that excludes fields marked with the {@link JsonExclude} annotation.
 * <p>
 * This strategy allows for selective exclusion of fields during serialization and deserialization
 * based on annotations.
 *
 * @author gregory.feijon
 */
@Component
public class GsonSerializationStrategy implements ExclusionStrategy {

    /**
     * Determines whether a field should be skipped during serialization/deserialization.
     * <p>
     * Fields with the {@link JsonExclude} annotation will be skipped.
     *
     * @param fieldAttributes The attributes of the field
     * @return true if the field should be skipped, false otherwise
     */
    @Override
    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
        return fieldAttributes.getAnnotation(JsonExclude.class) != null;
    }

    /**
     * Determines whether a class should be skipped during serialization/deserialization.
     * <p>
     * This implementation always returns false, meaning no classes are skipped.
     *
     * @param aClass The class to check
     * @return false, indicating that no classes should be skipped
     */
    @Override
    public boolean shouldSkipClass(Class<?> aClass) {
        return false;
    }
}