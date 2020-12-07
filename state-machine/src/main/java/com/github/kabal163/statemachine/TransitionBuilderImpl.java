package com.github.kabal163.statemachine;

import com.github.kabal163.statemachine.api.Action;
import com.github.kabal163.statemachine.api.Condition;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.apache.commons.lang3.ObjectUtils.allNotNull;

public class TransitionBuilderImpl<S, E> implements TransitionBuilder<S, E> {

    private final Set<Transition<S, E>> configuredTransitions = new HashSet<>();
    private Transition<S, E> configuredTransition;

    @Override
    public TransitionConfigurer<S, E> with() {
        configuredTransition = new Transition<>();
        configuredTransitions.add(configuredTransition);

        return this;
    }

    @Override
    public TransitionConfigurer<S, E> sourceState(S state) {
        Objects.requireNonNull(state, "sourceState must not be null!");
        checkConfiguredTransitionIsNotNull();

        configuredTransition.setSourceState(state);

        return this;
    }

    @Override
    public TransitionConfigurer<S, E> targetState(S state) {
        Objects.requireNonNull(state, "targetState must not be null!");
        checkConfiguredTransitionIsNotNull();

        configuredTransition.setTargetState(state);

        return this;
    }

    @Override
    public TransitionConfigurer<S, E> event(E event) {
        Objects.requireNonNull(event, "event must not be null!");
        checkConfiguredTransitionIsNotNull();

        configuredTransition.setEvent(event);

        return this;
    }

    @Override
    public TransitionConfigurer<S, E> condition(Condition<S, E> condition) {
        Objects.requireNonNull(condition, "Condition must not be null!");
        checkConfiguredTransitionIsNotNull();

        configuredTransition.addCondition(condition);

        return this;
    }

    @Override
    public TransitionConfigurer<S, E> action(Action<S, E> action) {
        Objects.requireNonNull(action, "Action must not be null!");
        checkConfiguredTransitionIsNotNull();

        configuredTransition.addAction(action);

        return this;
    }

    @Override
    public Set<Transition<S, E>> buildTransitions() {
        for (Transition<S, E> transition : configuredTransitions) {
            if (!allNotNull(
                    transition.getSourceState(),
                    transition.getTargetState(),
                    transition.getEvent())) {
                throw new IllegalStateException("Transition must have mandatory parameters - sourceState, targetState, event." +
                        "But something is null for transition: " + transition.getClass().getName());
            }
        }

        return new HashSet<>(configuredTransitions);
    }

    private void checkConfiguredTransitionIsNotNull() {
        if (configuredTransition == null) {
            throw new IllegalStateException(ERROR_MESSAGE);
        }
    }

    private static final String ERROR_MESSAGE = "Bad usage of a TransitionConfigurer. Probably you forget about with() method in start of transition configuration";
}
