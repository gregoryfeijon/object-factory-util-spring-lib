package io.github.gregoryfeijon;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

class TestObjectsFactory {

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
}
