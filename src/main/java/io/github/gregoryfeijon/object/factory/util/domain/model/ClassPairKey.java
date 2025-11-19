package io.github.gregoryfeijon.object.factory.util.domain.model;

/**
 * Simple immutable key representing a (sourceClass, destinationClass) pair.
 */
public record ClassPairKey(Class<?> sourceClass, Class<?> destClass) {
}