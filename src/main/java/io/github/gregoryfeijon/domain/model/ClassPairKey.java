package io.github.gregoryfeijon.domain.model;

/**
 * Simple immutable key representing a (sourceClass, destinationClass) pair.
 */
public record ClassPairKey(Class<?> sourceClass, Class<?> destClass) {
}