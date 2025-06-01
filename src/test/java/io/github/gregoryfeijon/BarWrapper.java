package io.github.gregoryfeijon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class BarWrapper {

    private PrimitiveBar primitiveBar;
    private ObjectBar objectBar;
    private List<PrimitiveBar> primitiveBarList;
    private Map<String, ObjectBar> objectBarMap;
}