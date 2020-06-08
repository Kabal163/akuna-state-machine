package com.github.kabal163.statemachine.api;

/**
 * The interface represents a guard which prevents
 * any transition if some conditions don't match with requirements.
 * If any condition returns false then the transition should be treated
 * as failed and should not be performed.
 * Implement this interface to define if the current transition is allowed
 * to be performed. Use it in your {@link LifecycleConfiguration} lifecycle configuration.
 *
 * @param <S> a state of a stateful object
 * @param <E> an event which triggers a transition
 */
public interface Condition<S, E> {

    /**
     * Defines if the current transition is allowed to be performed.
     * Method receives the context which contains information about the
     * current transition.
     *
     * @param context contains information about the current transition
     * @return true if all conditions are met requirements. Otherwise false
     */
    boolean evaluate(StateContext<S, E> context);
}
