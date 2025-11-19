package io.github.gregoryfeijon.object.factory.util.domain;

import io.github.gregoryfeijon.object.factory.util.domain.enums.StatusTestDest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyValueDest {

    private String sameType;
    private int wrapperToPrimitiveNull;
    private Integer primitiveToWrapperZero;
    private StatusTestDest status;
    private Set<Integer> listDifferentType;
    private Object fallback;
}