package com.github.kabal163.statemachine;

import com.github.kabal163.statemachine.api.LifecycleConfiguration;
import com.github.kabal163.statemachine.api.StatefulObject;
import com.github.kabal163.statemachine.exception.AmbiguousTransitionException;
import com.github.kabal163.statemachine.exception.LifecycleNotFoundException;
import com.github.kabal163.statemachine.exception.TransitionNotFoundException;

/**
 * Provides with transitions built from configurations.
 * Configurations can be specified by java config or json (not implemented yet)
 *
 * @param <S> type of the state of the {@link StatefulObject stateful object}
 * @param <E> type of event
 */
public interface TransitionProvider<S, E> {

    /**
     * Returns matching transition according the stateful object's source state
     * and the event. There must be exactly one transition which satisfies the
     * condition. Transitions grouped by lifecycle names provided
     * by {@link StatefulObject#getLifecycleName()} stateful object and
     * {@link LifecycleConfiguration#getLifecycleName()} lifecycle configuration
     *
     * @param statefulObject an object which state should be changed with
     *                       corresponding actions execution
     * @param event          a signal which helps to define the target state
     * @return transition which must be performed over the stateful object
     * @throws AmbiguousTransitionException if more then one matching transition is found
     * @throws TransitionNotFoundException  if no matching transition is found
     * @throws NullPointerException         if any of arguments is {@code null}
     * @throws LifecycleNotFoundException   if there is no transitions with lifecycle name which specified
     *                                      in the {@link StatefulObject#getLifecycleName()} stateful object
     *                                      or {@link StatefulObject#getLifecycleName()} returns {@code null} or empty string
     */
    Transition<S, E> getTransition(StatefulObject<S> statefulObject, E event);
}
