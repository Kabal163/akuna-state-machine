package com.github.kabal163.statemachine.api;

import com.github.kabal163.statemachine.TransitionConfigurer;

/**
 * The interface which must be implemented to configure
 * a lifecycle of a stateful object. Now there are no another
 * way to configure it. Use your {@link Action} actions and
 * {@link Condition} conditions to describe the lifecycle.
 *
 * @param <S> a state of a stateful object
 * @param <E> an event which triggers a transition
 */
public interface LifecycleConfiguration<S, E> {

    /**
     * Should describe the lifecycle of a stateful object.
     *
     * @param configurer is a convenient component which helps you
     *                   to configure the lifecycle
     */
    void configureTransitions(TransitionConfigurer<S, E> configurer);
}
