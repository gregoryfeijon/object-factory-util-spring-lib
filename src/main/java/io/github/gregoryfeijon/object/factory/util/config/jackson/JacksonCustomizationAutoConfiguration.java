package io.github.gregoryfeijon.object.factory.util.config.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gregoryfeijon.object.factory.util.config.jackson.factory.EnumDeserializers;
import io.github.gregoryfeijon.object.factory.util.config.jackson.factory.EnumSerializers;
import io.github.gregoryfeijon.object.factory.util.config.jackson.serialization.introspector.JsonExcludeIntrospector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Auto-configuration for Jackson serialization/deserialization customizations.
 * <p>
 * This configuration automatically registers custom enum serializers/deserializers
 * and the JsonExclude introspector when Jackson is on the classpath.
 * <p>
 * To disable this auto-configuration, add to your application.properties:
 * <pre>
 * spring.autoconfigure.exclude=com.yourcompany.yourlib.config.JacksonCustomizationAutoConfiguration
 * </pre>
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass({ObjectMapper.class, Jackson2ObjectMapperBuilder.class})
public class JacksonCustomizationAutoConfiguration {

    /**
     * Creates the EnumSerializers bean if not already defined.
     *
     * @return EnumSerializers instance
     */
    @Bean
    @ConditionalOnMissingBean
    public EnumSerializers enumSerializers() {
        log.debug("Creating EnumSerializers bean");
        return new EnumSerializers();
    }

    /**
     * Creates the EnumDeserializers bean if not already defined.
     *
     * @return EnumDeserializers instance
     */
    @Bean
    @ConditionalOnMissingBean
    public EnumDeserializers enumDeserializers() {
        log.debug("Creating EnumDeserializers bean");
        return new EnumDeserializers();
    }

    /**
     * Creates a custom Jackson module with enum serializers and deserializers.
     *
     * @param enumSerializers Custom enum serializers resolver
     * @param enumDeserializers Custom enum deserializers resolver
     * @return An EnumCustomizationModule configured with custom enum handling
     */
    @Bean
    @ConditionalOnMissingBean
    public EnumCustomizationModule enumCustomizationModule(
            EnumSerializers enumSerializers,
            EnumDeserializers enumDeserializers) {
        log.info("Creating EnumCustomizationModule");
        return new EnumCustomizationModule(enumSerializers, enumDeserializers);
    }

    /**
     * Creates a customizer that registers the custom module and annotation introspector.
     * <p>
     * This customizer is applied to all ObjectMapper instances created by Spring Boot.
     *
     * @param enumCustomizationModule The module containing custom enum handling
     * @return A Jackson2ObjectMapperBuilderCustomizer
     */
    @Bean
    @ConditionalOnMissingBean(name = "jacksonCustomizationCustomizer")
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizationCustomizer(
            EnumCustomizationModule enumCustomizationModule) {
        log.info("Registering Jackson customizations");

        return builder -> {
            // Register the custom module
            builder.modules(enumCustomizationModule);

            // Register the JsonExclude introspector
            builder.annotationIntrospector(new JsonExcludeIntrospector());

            log.info("Jackson customizations successfully applied");
        };
    }
}