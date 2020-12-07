package com.github.kabal163.statemachine;

import com.github.kabal163.statemachine.api.StatefulObject;

import java.util.Set;

/**
 * Specifies a lifecycle for a {@link StatefulObject stateful object}.
 * Lifecycle has a set of transitions which can be applied to the stateful object.
 * Lifecycle is like a set of rules which answers the question: "what we should to do with a
 * stateful object now by some event"
 *
 * @param <S> type of the state of the {@link StatefulObject stateful object}
 * @param <E> type of event
 * @see Transition
 */
public interface Lifecycle<S, E> {

    /**
     * @return returns the name of the lifecycle
     */
    String getName();

    /**
     * @return a set of transition
     * @see Transition
     */
    Set<Transition<S, E>> getTransitions();
}
