package io.github.gregoryfeijon.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OnlyStaticAttributeSource {

    private static final String IGNORED = "nope";
}
