package io.github.gregoryfeijon;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gregoryfeijon.config.jackson.EnumCustomizationModule;
import io.github.gregoryfeijon.config.jackson.JacksonCustomizationAutoConfiguration;
import io.github.gregoryfeijon.config.jackson.factory.EnumDeserializers;
import io.github.gregoryfeijon.config.jackson.factory.EnumSerializers;
import io.github.gregoryfeijon.domain.FilteredClassLoader;
import io.github.gregoryfeijon.domain.TestObjectForEnum;
import io.github.gregoryfeijon.domain.enums.TestEnum;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link JacksonCustomizationAutoConfiguration}.
 */
class JacksonCustomizationAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    JacksonAutoConfiguration.class,
                    JacksonCustomizationAutoConfiguration.class
            ));

    @Test
    void shouldRegisterEnumSerializers() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(EnumSerializers.class);
            assertThat(context.getBean(EnumSerializers.class)).isNotNull();
        });
    }

    @Test
    void shouldRegisterEnumDeserializers() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(EnumDeserializers.class);
            assertThat(context.getBean(EnumDeserializers.class)).isNotNull();
        });
    }

    @Test
    void shouldRegisterEnumCustomizationModule() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(EnumCustomizationModule.class);
            assertThat(context.getBean(EnumCustomizationModule.class)).isNotNull();
        });
    }

    @Test
    void shouldRegisterJacksonCustomizer() {
        contextRunner.run(context -> {
            assertThat(context).hasBean("jacksonCustomizationCustomizer");
        });
    }

    @Test
    void shouldConfigureObjectMapperWithCustomModule() {
        contextRunner.run(context -> {
            ObjectMapper objectMapper = context.getBean(ObjectMapper.class);
            assertThat(objectMapper).isNotNull();

            // Functional test: verify custom enum serialization works
            // Use a DTO wrapping the enum to ensure proper contextualization
            TestObjectForEnum dto = new TestObjectForEnum(TestEnum.TEST_VALUE);
            String json = objectMapper.writeValueAsString(dto);

            // If the module is registered, custom serialization should work
            assertThat(json).isNotNull();
            assertThat(json).contains("TEST_VALUE");

            // Deserialize back
            TestObjectForEnum deserialized = objectMapper.readValue(json, TestObjectForEnum.class);
            assertThat(deserialized).isNotNull();
            assertThat(deserialized.getStatus()).isEqualTo(TestEnum.TEST_VALUE);
        });
    }

    // Test DTO to wrap the enum


    // Simple test enum for verification


    @Test
    void shouldAllowCustomBeansToOverride() {
        contextRunner
                .withBean("enumSerializers", EnumSerializers.class, () -> new EnumSerializers())
                .run(context -> {
                    assertThat(context).hasSingleBean(EnumSerializers.class);
                });
    }

    @Test
    void shouldNotRegisterWhenJacksonNotPresent() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(
                        JacksonCustomizationAutoConfiguration.class
                ))
                .withClassLoader(new FilteredClassLoader(ObjectMapper.class))
                .run(context -> {
                    assertThat(context).doesNotHaveBean(EnumSerializers.class);
                    assertThat(context).doesNotHaveBean(EnumDeserializers.class);
                    assertThat(context).doesNotHaveBean(EnumCustomizationModule.class);
                });
    }
}