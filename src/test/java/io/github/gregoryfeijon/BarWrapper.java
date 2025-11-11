package io.github.gregoryfeijon;

import io.github.gregoryfeijon.domain.annotation.FieldCopyName;
import io.github.gregoryfeijon.domain.annotation.ObjectConstructor;
import io.github.gregoryfeijon.domain.annotation.ObjectCopyExclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ObjectConstructor(exclude = {"fieldExcluded"})
class BarWrapper {

    private PrimitiveBar primitiveBar;
    private ObjectBar objectBar;
    private List<PrimitiveBar> primitiveBarList;
    private Map<String, ObjectBar> objectBarMap;
    private String fieldExcluded;
    private String fieldExcludedWithAnnotation;

    @ObjectCopyExclude
    @FieldCopyName("fieldExcludedWithAnnotationInDest")
    private String fieldExcludedWithAnnotationInDestNameModified;

    @FieldCopyName("alternativeName")
    private String fieldExcludedUsingClassLevelAnnotation;
}