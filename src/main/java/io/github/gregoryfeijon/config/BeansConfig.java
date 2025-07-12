package io.github.gregoryfeijon.config;

import io.github.gregoryfeijon.domain.properties.SerializerProviderProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for defining custom beans in the application.
 * <p>
 * This class can be used to define and configure Spring beans programmatically
 * when annotation-based configuration is not sufficient.
 *
 * @author gregory.feijon
 * @since 01/05/2025
 */

@Configuration
public class BeansConfig {

    /**
     * Creates and configures a SerializerProviderProperties bean.
     * <p>
     * This bean is configured from properties with the prefix "serializer.provider.default"
     * in application configuration files (e.g., application.properties or application.yml).
     * It provides configuration options for the serialization framework, including which
     * serializer type to use (Gson or Jackson) and whether serialization is enabled.
     *
     * @return A configured SerializerProviderProperties instance
     */
    @Bean
    @ConfigurationProperties(value = "serializer.provider.default")
    public SerializerProviderProperties serializerProviderProperties() {
        return new SerializerProviderProperties();
    }
}