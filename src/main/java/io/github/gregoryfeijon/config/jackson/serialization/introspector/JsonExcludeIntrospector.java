package io.github.gregoryfeijon.config.jackson.serialization.introspector;

import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import io.github.gregoryfeijon.domain.annotation.JsonExclude;

public class JsonExcludeIntrospector extends JacksonAnnotationIntrospector {

    @Override
    public boolean hasIgnoreMarker(AnnotatedMember member) {
        return member.hasAnnotation(JsonExclude.class) || super.hasIgnoreMarker(member);
    }
}
