package io.github.gregoryfeijon;

import io.github.gregoryfeijon.domain.annotation.FieldCopyName;
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
class FooWrapper {

    @FieldCopyName(value = "primitiveBar")
    private PrimitiveFoo primitiveFoo;

    @FieldCopyName(value = "objectBar")
    private ObjectFoo objectFoo;

    @FieldCopyName(value = "primitiveBarList")
    private List<PrimitiveFoo> primitiveFooList;

    @FieldCopyName(value = "objectBarMap")
    private Map<String, ObjectFoo> objectFooMap;

    private String fieldExcluded;

    @ObjectCopyExclude
    private String fieldExcludedWithAnnotation;
}