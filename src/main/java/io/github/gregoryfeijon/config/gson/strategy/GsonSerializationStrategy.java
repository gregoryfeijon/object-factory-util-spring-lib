package io.github.gregoryfeijon.config.gson.strategy;

import io.github.gregoryfeijon.domain.annotation.Exclude;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import org.springframework.stereotype.Component;

@Component
public class GsonSerializationStrategy implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
        return fieldAttributes.getAnnotation(Exclude.class) != null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> aClass) {
        return false;
    }
}
