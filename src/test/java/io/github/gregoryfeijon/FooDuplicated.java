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
class FooDuplicated {

    @FieldCopyName("duplicado")
    private String nome1;

    @FieldCopyName("duplicado")
    private String nome2;
}