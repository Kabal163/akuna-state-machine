package com.github.kabal163.statemachine.api;

/**
 * The interface provides simple abstraction for a piece of work
 * which should be performed to transit a stateful object to the
 * target state according the event. If any action fails then
 * the whole transition should be treated as failed.
 * Implement this interface and put there any logic which is needed
 * during a transition. Use it in your {@link LifecycleConfiguration}
 * lifecycle configuration.
 *
 * @param <S> type of the state of the {@link StatefulObject stateful object}
 * @param <E> type of event
 */
public interface Action<S, E> {

    /**
     * Performs a unit of work to execute transition
     * from source to target. Method receives the context
     * which contains information about the current transition.
     *
     * @param context contains information about the current transition
     */
    void execute(StateContext<S, E> context);
}
