package io.github.gregoryfeijon;

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
class ObjectFoo {

    private Integer integerValue;
    private String stringValue;
    private BigDecimal bigDecimalValue;
}