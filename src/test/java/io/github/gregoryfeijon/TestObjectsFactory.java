package io.github.gregoryfeijon;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class TestObjectsFactory {

    static PrimitiveFoo createPrimitiveFoo() {
        return PrimitiveFoo.builder()
                .intValue(1)
                .longValue(2L)
                .boolValue(true)
                .build();
    }

    static PrimitiveBar createPrimitiveBar() {
        return PrimitiveBar.builder()
                .iVal(1)
                .longValue(2L)
                .boolValue(true)
                .build();
    }

    static ObjectFoo createObjectFoo() {
        return ObjectFoo.builder()
                .integerValue(10)
                .stringValue("foo")
                .bigDecimalValue(BigDecimal.valueOf(100.5))
                .build();
    }

    static ObjectBar createObjectBar() {
        return ObjectBar.builder()
                .integerValue(10)
                .stringValue("foo")
                .bdValue(BigDecimal.valueOf(100.5))
                .build();
    }

    static FooWrapper createFooWrapper() {
        return FooWrapper.builder()
                .primitiveFoo(createPrimitiveFoo())
                .objectFoo(createObjectFoo())
                .primitiveFooList(List.of(createPrimitiveFoo()))
                .objectFooMap(Map.of("key", createObjectFoo()))
                .build();
    }

    static BarWrapper createBarWrapper() {
        return BarWrapper.builder()
                .primitiveBar(createPrimitiveBar())
                .objectBar(createObjectBar())
                .primitiveBarList(List.of(createPrimitiveBar()))
                .objectBarMap(Map.of("key", createObjectBar()))
                .build();
    }

    static PrimitiveArrayHolder createPrimitiveArrayHolder() {
        return PrimitiveArrayHolder.builder()
                .intValues(new int[]{1, 2, 3})
                .build();
    }

    static WrapperArrayHolder createWrapperArrayHolder() {
        return WrapperArrayHolder.builder()
                .integerValues(new Integer[]{1, 2, 3})
                .build();
    }

    static NonSerializableObject createNonSerializableObject() {
        return NonSerializableObject.builder()
                .thread(new Thread())
                .build();
    }

    static FooDuplicated createFooDuplicatedObject() {
        return FooDuplicated.builder()
                .nome1("nome 1 duplicado")
                .nome2("nome 2 duplicado")
                .build();
    }

    static VerifyValueSource createVerifyValueSource() {
        return VerifyValueSource.builder()
                .sameType("ok")
                .wrapperToPrimitiveNull(null)
                .primitiveToWrapperZero(0)
                .status(StatusTestSource.ACTIVE)
                .listDifferentType(List.of("a", "b"))
                .fallback("stringFallback")
                .build();
    }
}
