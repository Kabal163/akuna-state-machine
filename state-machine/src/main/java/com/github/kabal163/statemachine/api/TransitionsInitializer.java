package com.github.kabal163.statemachine.api;

import com.github.kabal163.statemachine.Transition;
import com.github.kabal163.statemachine.JavaConfigTransitionsInitializer;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Provides convenient method to build transitions
 * according the provided configurations.
 * Use it's default {@link JavaConfigTransitionsInitializer} implementation
 * to build transitions from your configurations.
 */
public interface TransitionsInitializer {

    /**
     * Builds and returns transitions according the provided configurations.
     * Each configuration provides a lifecycle's name. Method will build transitions
     * for each configuration and group them by lifecycle's name.
     * Configurations must not be null or empty collection.
     *
     * @param configurations which describe lifecycles for stateful objects
     * @return transitions grouped by lifecycle's name
     * @throws IllegalArgumentException if configurations is null or empty collection
     */
    Map<String, Set<Transition>> initialize(Collection<LifecycleConfiguration> configurations);
}
