package br.com.feijon.gregory.spring.lib.domain.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 04/04/2025 às 22:17
 *
 * @author gregory.feijon
 */

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum SerializationType {

    GSON("gson"), JACKSON("jackson");

    private final String description;
}
