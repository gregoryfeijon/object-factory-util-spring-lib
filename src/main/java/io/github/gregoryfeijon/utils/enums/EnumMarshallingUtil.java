package io.github.gregoryfeijon.utils.enums;

import io.github.gregoryfeijon.domain.annotation.EnumUseAttributeInMarshalling;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 08/11/2025 às 19:48
 *
 * @author gregory.feijon
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnumMarshallingUtil {

    /**
     * Extracts the attribute name to use from the annotation.
     * <p>
     * Prioritizes in order: serializeAttributeName, deserializeAttributeName, defaultAttributeName.
     *
     * @param useAttribute The annotation
     * @return The attribute name, or null if none is specified
     */
    public static String getAttributeName(EnumUseAttributeInMarshalling useAttribute) {
        if (useAttribute != null) {
            String serializeAttributeName = useAttribute.serializeAttributeName();
            String deserializeAttributeName = useAttribute.deserializeAttributeName();
            String defaultAttributeName = useAttribute.defaultAttributeName();

            if (!serializeAttributeName.isEmpty()) {
                return serializeAttributeName;
            }

            if (!deserializeAttributeName.isEmpty()) {
                return deserializeAttributeName;
            }

            if (!defaultAttributeName.isEmpty()) {
                return defaultAttributeName;
            }
        }
        return null;
    }
}
