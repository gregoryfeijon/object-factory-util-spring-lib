package io.github.gregoryfeijon.object.factory.util.domain;

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
public class PrimitiveFoo {

    private int intValue;
    private long longValue;
    private boolean boolValue;
}