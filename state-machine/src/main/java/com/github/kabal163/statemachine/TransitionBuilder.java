package com.github.kabal163.statemachine;

import com.github.kabal163.statemachine.api.LifecycleConfiguration;
import com.github.kabal163.statemachine.api.StatefulObject;

import java.util.Set;

/**
 * Builds a set of transitions according the {@link LifecycleConfiguration lifecycle configuration}
 *
 * @param <S> type of the state of the {@link StatefulObject stateful object}
 * @param <E> type of event
 * @see LifecycleConfiguration
 * @see TransitionConfigurer
 */
interface TransitionBuilder<S, E> extends TransitionConfigurer<S, E> {

    /**
     * Builds and returns a set of transitions according the {@link LifecycleConfiguration lifecycle configuration}.
     * If there is no transitions specified by the configuration then returns empty set.
     *
     * @return set of transitions
     */
    Set<Transition<S, E>> buildTransitions();
}
