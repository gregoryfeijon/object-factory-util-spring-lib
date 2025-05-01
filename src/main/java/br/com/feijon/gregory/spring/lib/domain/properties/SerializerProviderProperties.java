package br.com.feijon.gregory.spring.lib.domain.properties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SerializerProviderProperties {

    private boolean enabled;
    private String type;
}
