package io.github.gregoryfeijon.object.factory.util.domain;

import io.github.gregoryfeijon.object.factory.util.domain.annotation.FieldCopyName;
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
public class PrimitiveBar {

    @FieldCopyName(value = "intValue")
    private int iVal;
    private long longValue;
    private boolean boolValue;
}