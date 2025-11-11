package io.github.gregoryfeijon;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
class OnlyStaticAttributeSource {

    private static final String IGNORED = "nope";
}
