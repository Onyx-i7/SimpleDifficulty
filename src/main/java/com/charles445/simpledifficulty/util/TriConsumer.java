package com.charles445.simpledifficulty.util;

/**
 * Functional interface representing a consumer that accepts three arguments.
 */
@FunctionalInterface
public interface TriConsumer<A, B, C> {
    void accept(A a, B b, C c);
}