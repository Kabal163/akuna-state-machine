package com.github.kabal163.statemachine.api;

import com.github.kabal163.statemachine.exception.AmbiguousTransitionException;
import com.github.kabal163.statemachine.exception.TransitionNotFoundException;

import java.util.Map;

/**
 * Provides lifecycle functionality for a project. This is the entry point
 * for executing a transition on a stateful object. The {@link LifecycleManager}
 * uses internally {@link LifecycleConfiguration} lifecycle configuration
 * which must be configured by a user. If any exception occurs during a transition
 * will be placed into the {@link TransitionResult} transition result.
 *
 * @param <S> a state of a stateful object
 * @param <E> an event which triggers a transition
 */
public interface LifecycleManager<S, E> {

    /**
     * Executes a transition of the stateful object according the event.
     *
     * @param statefulObject an object which state should be changed with
     *                       corresponding actions execution
     * @param event a signal which helps to define the target state
     * @return {@link TransitionResult} transition result containing information
     * about executed transition
     * @throws TransitionNotFoundException if no transition was found for corresponding
     * stateful object's source state and event
     * @throws AmbiguousTransitionException if there are more then one matching transitions
     */
    TransitionResult<S, E> execute(StatefulObject<S> statefulObject, E event);

    /**
     * Executes a transition of the stateful object according the event.
     * The method contains an additional map of variables parameter which is
     * used in the actions and conditions during a transition.
     *
     * @param statefulObject an object which state should be changed with
     *                       corresponding actions execution
     * @param event a signal which helps to define the target state
     * @param variables any external data which is necessary during transition performance
     * @return {@link TransitionResult} transition result containing information
     * about executed transition
     * @throws TransitionNotFoundException if no transition was found for corresponding
     * stateful object's source state and event
     * @throws AmbiguousTransitionException if there are more then one matching transitions
     */
    TransitionResult<S, E> execute(StatefulObject<S> statefulObject, E event, Map<String, Object> variables);
}
