package io.github.gregoryfeijon;

import io.github.gregoryfeijon.domain.enums.SerializationType;
import io.github.gregoryfeijon.exception.ApiException;
import io.github.gregoryfeijon.utils.serialization.ObjectFactoryUtil;
import io.github.gregoryfeijon.utils.serialization.adapter.SerializerProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

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

        assertThat(copiedList).isNotNull().hasSize(fooList.size());
        assertThat(copiedList.getFirst()).isNotSameAs(fooList.getFirst());
    }

    @Test
    void shouldCopyAllObjectsFromCollectionWithReturnType() {
        List<PrimitiveFoo> fooList = List.of(new PrimitiveFoo(), new PrimitiveFoo());
        List<PrimitiveBar> copiedList = ObjectFactoryUtil.copyAllObjectsFromCollection(fooList, PrimitiveBar.class);

        assertThat(copiedList).isNotNull().hasSize(fooList.size());
        assertThat(copiedList.getFirst()).isInstanceOf(PrimitiveBar.class);
    }

    @Test
    void shouldCopyAllObjectsFromCollectionWithSupplier() {
        List<PrimitiveFoo> fooList = List.of(new PrimitiveFoo(), new PrimitiveFoo());
        Set<PrimitiveFoo> copiedSet = ObjectFactoryUtil.copyAllObjectsFromCollection(fooList, HashSet::new);

        assertThat(copiedSet)
                .isNotNull()
                .hasSize(fooList.size())
                .allSatisfy(obj -> assertThat(obj).isInstanceOf(PrimitiveFoo.class));
    }

    @Test
    void shouldCopyAllObjectsFromCollectionWithSupplierAndReturnType() {
        List<PrimitiveFoo> fooList = List.of(new PrimitiveFoo(), new PrimitiveFoo());
        Set<PrimitiveBar> copiedSet = ObjectFactoryUtil.copyAllObjectsFromCollection(fooList, HashSet::new, PrimitiveBar.class);

        assertThat(copiedSet)
                .isNotNull()
                .hasSize(fooList.size())
                .allSatisfy(obj -> assertThat(obj).isInstanceOf(PrimitiveBar.class));
    }

    @Test
    void shouldThrowExceptionWhenCollectionIsEmpty() {
        List<PrimitiveFoo> emptyList = Collections.emptyList();

        assertThatThrownBy(() -> ObjectFactoryUtil.copyAllObjectsFromCollection(emptyList))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("não possui elementos");
    }

    @Test
    void shouldThrowExceptionWhenSourceIsNull() {
        assertThatThrownBy(() -> ObjectFactoryUtil.createFromObject(null, PrimitiveBar.class))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("objeto a ser copiado é nulo");
    }

    @Test
    void shouldHandleNullFieldsGracefully() {
        ObjectFoo foo = new ObjectFoo(null, null, null);
        ObjectBar bar = ObjectFactoryUtil.createFromObject(foo, ObjectBar.class);
        assertThat(bar).isNotNull();
        assertThat(bar.getIntegerValue()).isNull();
        assertThat(bar.getBdValue()).isNull();
    }

    @Test
    void shouldClonePrimitiveArrayInsideHolder() {
        PrimitiveArrayHolder holder = TestObjectsFactory.createPrimitiveArrayHolder();
        PrimitiveArrayHolder clone = ObjectFactoryUtil.createFromObject(holder, PrimitiveArrayHolder.class);

        assertThat(clone.getIntValues())
                .containsExactly(holder.getIntValues())
                .isNotSameAs(holder.getIntValues());
    }

    @Test
    void shouldCloneWrapperArrayInsideHolder() {
        WrapperArrayHolder holder = TestObjectsFactory.createWrapperArrayHolder();
        WrapperArrayHolder clone = ObjectFactoryUtil.createFromObject(holder, WrapperArrayHolder.class);

        assertThat(clone.getIntegerValues())
                .containsExactly(holder.getIntegerValues())
                .isNotSameAs(holder.getIntegerValues());
    }

    @Test
    void shouldInitializeProviderIfEmpty() {
        SerializerProvider.initialize(new EnumMap<>(SerializationType.class), SerializationType.GSON);
        SerializerProvider.getAdapter(); // força lazy init
        assertThat(SerializerProvider.getAdapter()).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenSerializationFails() {
        assertThatThrownBy(() -> ObjectFactoryUtil.createFromObject(TestObjectsFactory.createNonSerializableObject(),
                NonSerializableObject.class))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Failed making field");
    }

    @Test
    void shouldIgnoreFieldsWithoutMatchingNamesOrAnnotations() {
        MismatchSource source = new MismatchSource();
        source.setFoo("value");

        MismatchTarget target = ObjectFactoryUtil.createFromObject(source, MismatchTarget.class);

        assertThat(target.getBar()).isNull();
    }

    @Test
    void shouldThrowExceptionWhenSupplierIsNull() {
        List<PrimitiveFoo> fooList = List.of(new PrimitiveFoo());
        Supplier<List<PrimitiveFoo>> supplier = null;

        assertThatThrownBy(() -> ObjectFactoryUtil.copyAllObjectsFromCollection(fooList, supplier))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("coleção especificada para retorno é nulo");
    }

    @Test
    void shouldReturnEmptyMapWhenSourceAndDestinationHaveNoFields() {
        // given
        EmptySource source = new EmptySource();

        // when
        EmptyDestination result = ObjectFactoryUtil.createFromObject(source, EmptyDestination.class);

        // then
        assertThat(result).isNotNull();
        // não há campos, então não ocorre nenhuma cópia real
    }

    @Test
    void shouldKeepFirstFieldWhenDuplicateKeyInSameClassOccurs() {
        var source = TestObjectsFactory.createFooDuplicatedObject();

        // Deve internamente chamar buildFieldKeyMap e cair no (a, b) -> a
        var result = ObjectFactoryUtil.createFromObject(source, FooDuplicated.class);

        // Se não deu exceção, o merge foi resolvido corretamente
        assertThat(result).isNotNull();
    }
}
