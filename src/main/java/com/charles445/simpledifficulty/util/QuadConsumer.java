package com.charles445.simpledifficulty.util;

/**
 * Functional interface representing a consumer that accepts four arguments.
 */
@FunctionalInterface
public interface QuadConsumer<A, B, C, D> {
    void accept(A a, B b, C c, D d);
}