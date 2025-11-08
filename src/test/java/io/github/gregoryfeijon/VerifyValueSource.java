package io.github.gregoryfeijon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class VerifyValueSource {

    private String sameType;
    private Integer wrapperToPrimitiveNull;
    private int primitiveToWrapperZero;
    private StatusTestSource status;
    private List<String> listDifferentType;
    private String fallback;
}