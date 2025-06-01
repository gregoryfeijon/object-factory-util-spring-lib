package io.github.gregoryfeijon;

import io.github.gregoryfeijon.domain.annotation.FieldCopyName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class PrimitiveBar {

    @FieldCopyName(value = "intValue")
    private int iVal;
    private long longValue;
    private boolean boolValue;
}