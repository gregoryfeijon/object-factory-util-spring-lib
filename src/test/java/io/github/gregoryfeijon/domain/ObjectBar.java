package io.github.gregoryfeijon.domain;

import io.github.gregoryfeijon.domain.annotation.FieldCopyName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObjectBar {

    private Integer integerValue;
    private String stringValue;

    @FieldCopyName(value = "bigDecimalValue")
    private BigDecimal bdValue;
}