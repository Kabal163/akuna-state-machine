package com.github.kabal163.statemachine.api;

import com.github.kabal163.statemachine.TransitionConfigurer;

/**
 * The interface which must be implemented to configure
 * a lifecycle of a stateful object. Now there are no another
 * way to configure it. Use your {@link Action} actions and
 * {@link Condition} conditions to describe the lifecycle.
 */
public interface LifecycleConfiguration {

    /**
     * Should describe the lifecycle of a stateful object.
     *
     * @param configurer is a convenient component which helps you
     *                   to configure the lifecycle
     */
    void configureTransitions(TransitionConfigurer configurer);

    /**
     * Should return the name of the configured lifecycle.
     * Each lifecycle must have a unique name.
     * If method returns <code>null</code> or empty string
     * then lifecycle's name will be the same as the it's
     * configuration class name.
     *
     * @return lifecycle's name
     */
    default String getLifecycleName() {
        return null;
    }
}
