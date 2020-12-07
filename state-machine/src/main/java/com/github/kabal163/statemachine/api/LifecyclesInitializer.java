package com.github.kabal163.statemachine.api;

import com.github.kabal163.statemachine.JavaConfigLifecyclesInitializer;
import com.github.kabal163.statemachine.Lifecycle;

import java.util.Collection;
import java.util.Map;

/**
 * Provides convenient method to build lifecycles
 * according the provided configurations.
 * Use it's default {@link JavaConfigLifecyclesInitializer} implementation
 * to build lifecycles from your configurations.
 */
public interface LifecyclesInitializer {

    /**
     * Builds and returns lifecycles according the provided configurations.
     * Each configuration should provide a lifecycle's name {@link LifecycleConfiguration#getLifecycleName()}.
     *
     * @param configurations describes lifecycles for stateful objects
     * @return built lifecycles
     * @throws NullPointerException if configurations is null or empty collection
     */
    <S, E> Map<String, Lifecycle<S, E>> initialize(Collection<LifecycleConfiguration<S, E>> configurations);
}
