package io.github.gregoryfeijon;

import io.github.gregoryfeijon.exception.ApiException;
import io.github.gregoryfeijon.utils.serialization.ObjectFactoryUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObjectFactoryUtilTest {

    @BeforeAll
    static void setup() {
        TestSerializerUtil.configureGsonAndJacksonAdapter();
    }

    @Test
    void shouldCopyPrimitiveFieldsUsingAnnotationAndNames() {
        PrimitiveFoo foo = new PrimitiveFoo();
        PrimitiveBar bar = ObjectFactoryUtil.createFromObject(foo, PrimitiveBar.class);

        assertThat(bar).isNotNull();
        assertThat(bar.getIVal()).isEqualTo(foo.getIntValue());
        assertThat(bar.getLongValue()).isEqualTo(foo.getLongValue());
        assertThat(bar.isBoolValue()).isEqualTo(foo.isBoolValue());
    }

    @Test
    void shouldCopyObjectFieldsWithAnnotation() {
        ObjectFoo foo = new ObjectFoo();
        ObjectBar bar = ObjectFactoryUtil.createFromObject(foo, ObjectBar.class);

        assertThat(bar).isNotNull();
        assertThat(bar.getIntegerValue()).isEqualTo(foo.getIntegerValue());
        assertThat(bar.getStringValue()).isEqualTo(foo.getStringValue());
        assertThat(bar.getBdValue()).isEqualTo(foo.getBigDecimalValue());
    }

    @Test
    void shouldCopyWrapperWithCollections() {
        FooWrapper fooWrapper = new FooWrapper();
        fooWrapper.setPrimitiveFoo(new PrimitiveFoo());
        fooWrapper.setObjectFoo(new ObjectFoo());
        fooWrapper.setPrimitiveFooList(List.of(new PrimitiveFoo()));
        fooWrapper.setObjectFooMap(Map.of("key", new ObjectFoo()));

        BarWrapper barWrapper = ObjectFactoryUtil.createFromObject(fooWrapper, BarWrapper.class);

        assertThat(barWrapper).isNotNull();
        assertThat(barWrapper.getPrimitiveBar()).isNotNull();
        assertThat(barWrapper.getObjectBar()).isNotNull();
        assertThat(barWrapper.getPrimitiveBarList()).hasSize(1);
        assertThat(barWrapper.getObjectBarMap()).containsKey("key");
    }

    @Test
    void shouldNotFailOnMissingDestFields() {
        PrimitiveFoo foo = new PrimitiveFoo();


        PartialBar partialBar = ObjectFactoryUtil.createFromObject(foo, PartialBar.class);

        assertThat(partialBar).isNotNull();
        assertThat(partialBar.getSomeOtherField()).isZero();
    }

    @Test
    void shouldCopyAllObjectsFromCollection() {
        List<PrimitiveFoo> fooList = List.of(new PrimitiveFoo(), new PrimitiveFoo());
        List<PrimitiveFoo> copiedList = ObjectFactoryUtil.copyAllObjectsFromCollection(fooList);

        assertThat(copiedList).isNotNull();
        assertThat(copiedList).hasSize(fooList.size());
        assertThat(copiedList.getFirst()).isNotSameAs(fooList.getFirst());
    }

    @Test
    void shouldCopyAllObjectsFromCollectionWithReturnType() {
        List<PrimitiveFoo> fooList = List.of(new PrimitiveFoo(), new PrimitiveFoo());
        List<PrimitiveBar> copiedList = ObjectFactoryUtil.copyAllObjectsFromCollection(fooList, PrimitiveBar.class);

        assertThat(copiedList).isNotNull();
        assertThat(copiedList).hasSize(fooList.size());
        assertThat(copiedList.getFirst()).isInstanceOf(PrimitiveBar.class);
    }

    @Test
    void shouldCopyAllObjectsFromCollectionWithSupplier() {
        List<PrimitiveFoo> fooList = List.of(new PrimitiveFoo(), new PrimitiveFoo());
        Set<PrimitiveFoo> copiedSet = ObjectFactoryUtil.copyAllObjectsFromCollection(fooList, HashSet::new);

        assertThat(copiedSet).isNotNull();
        assertThat(copiedSet).hasSize(fooList.size());
        assertThat(copiedSet).allSatisfy(obj -> assertThat(obj).isInstanceOf(PrimitiveFoo.class));
    }

    @Test
    void shouldCopyAllObjectsFromCollectionWithSupplierAndReturnType() {
        List<PrimitiveFoo> fooList = List.of(new PrimitiveFoo(), new PrimitiveFoo());
        Set<PrimitiveBar> copiedSet = ObjectFactoryUtil.copyAllObjectsFromCollection(fooList, HashSet::new, PrimitiveBar.class);

        assertThat(copiedSet).isNotNull();
        assertThat(copiedSet).hasSize(fooList.size());
        assertThat(copiedSet).allSatisfy(obj -> assertThat(obj).isInstanceOf(PrimitiveBar.class));
    }

    @Test
    void shouldThrowExceptionWhenCollectionIsEmpty() {
        List<PrimitiveFoo> emptyList = Collections.emptyList();

        assertThatThrownBy(() -> ObjectFactoryUtil.copyAllObjectsFromCollection(emptyList))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("não possui elementos");
    }
}
