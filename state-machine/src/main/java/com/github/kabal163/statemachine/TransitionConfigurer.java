package com.github.kabal163.statemachine;

import com.github.kabal163.statemachine.api.Action;
import com.github.kabal163.statemachine.api.Condition;
import com.github.kabal163.statemachine.api.StatefulObject;

/**
 * Convenient component which helps to configure the lifecycle effortless.
 * The important thing is the {@code with()} method must be called each time
 * before you started to describe a transition.
 * E.x. {@code configurer.with().sourceState("NEW").targetState("APPROVED")...}
 * For more details see: https://github.com/Kabal163/akuna-state-machine/tree/master/state-machine-spring-boot-samples
 * Any configured transition must contain at least {@code sourceState}, {@code targetState} and {@code event}
 * The other attributes are optional
 *
 * @param <S> type of the state of the {@link StatefulObject stateful object}
 * @param <E> type of event
 */
public interface TransitionConfigurer<S, E> {

    /**
     * Used to define the new one described transition.
     * Must be call each time before starting transition describing
     *
     * @return the configurer instance
     */
    TransitionConfigurer<S, E> with();

    /**
     * Defines a state which must correspond to the stateful object's state
     * at the moment of start a transition. This is the mandatory attribute
     * which must be specified for each transition. The {@code state}
     * must be neither {@code null} nor empty string otherwise {@link IllegalStateException} will be
     * thrown while building a transition.
     *
     * @param state state which must correspond to the stateful object's state
     *              at the moment of start a transition
     * @return the configurer instance
     * @throws NullPointerException if {@code state} is {@code null}
     */
    TransitionConfigurer<S, E> sourceState(S state);

    /**
     * Defines a desired state which should be on the stateful object
     * after transition finish. This is the mandatory attribute
     * which must be specified for each transition. The {@code state}
     * must be neither {@code null} nor empty string otherwise {@link IllegalStateException} will be
     * thrown while building a transition.
     *
     * @param state the desired state which should be on the stateful object
     *              after transition finish
     * @return the configurer instance
     * @throws NullPointerException if {@code state} is {@code null}
     */
    TransitionConfigurer<S, E> targetState(S state);

    /**
     * Defines a trigger which along with the source state defines the transition
     * which must be executed. This is the mandatory attribute which must be
     * specified for each transition. The {@code event}
     * must be neither {@code null} nor empty string otherwise {@link IllegalStateException} will be
     * thrown while building a transition.
     *
     * @param event a trigger which defines a cause of transition execution
     * @return the configurer instance
     * @throws NullPointerException if {@code event} is {@code null}
     */
    TransitionConfigurer<S, E> event(E event);

    /**
     * Defines a guard which prevents transition execution. Each transition
     * can contain as many conditions as it needs. If any condition
     * returns false then the whole transition should be treated as
     * impossible and should not be executed. This is optional attribute
     * and can be omitted.
     *
     * @param condition a guard which prevents transition execution
     * @return the configurer instance
     * @throws NullPointerException if {@code condition} is {@code null}
     */
    TransitionConfigurer<S, E> condition(Condition<S, E> condition);

    /**
     * Defines a piece of work which must be performed in order to transit
     * the stateful object to the target state. If any action throws an
     * exception then the whole transition should be treated as failed and
     * stop it's execution. This is optional attribute and can be omitted.
     *
     * @param action a piece of work which must be performed in order to transit
     *               the stateful object to the target state
     * @return the configurer instance
     * @throws NullPointerException if {@code action} is {@code null}
     */
    TransitionConfigurer<S, E> action(Action<S, E> action);
}
