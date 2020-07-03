package com.github.kabal163.statemachine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.github.kabal163.statemachine.api.Action;
import com.github.kabal163.statemachine.api.Condition;

import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang3.ObjectUtils.allNotNull;

@Slf4j
@RequiredArgsConstructor
public class TransitionBuilderImpl implements TransitionBuilder {

    private final Set<Transition> configuredTransitions = new HashSet<>();
    private Transition configuredTransition;

    @Override
    public TransitionConfigurer with() {
        configuredTransition = new Transition();
        configuredTransitions.add(configuredTransition);

        return this;
    }

    @Override
    public TransitionConfigurer sourceState(String state) {
        assertConfiguredTransitionIsNotNull();
        configuredTransition.setSourceState(state);

        return this;
    }

    @Override
    public TransitionConfigurer targetState(String state) {
        assertConfiguredTransitionIsNotNull();
        configuredTransition.setTargetState(state);

        return this;
    }

    @Override
    public TransitionConfigurer event(String event) {
        assertConfiguredTransitionIsNotNull();
        configuredTransition.setEvent(event);

        return this;
    }

    @Override
    public TransitionConfigurer condition(Condition condition) {
        assertConfiguredTransitionIsNotNull();
        if (condition == null) {
            throw new IllegalArgumentException("Condition must not be null!");
        }
        configuredTransition.addCondition(condition);

        return this;
    }

    @Override
    public TransitionConfigurer action(Action action) {
        assertConfiguredTransitionIsNotNull();
        if (action == null) {
            throw new IllegalArgumentException("Action must not be null!");
        }
        configuredTransition.addAction(action);

        return this;
    }

    @Override
    public Set<Transition> buildTransitions() {
        for (Transition transition : configuredTransitions) {
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

    private void assertConfiguredTransitionIsNotNull() {
        if (configuredTransition == null) {
            throw new IllegalStateException(ERROR_MESSAGE);
        }
    }

    private static final String ERROR_MESSAGE = "Bad usage of a TransitionConfigurer. Probably you forget about with() method in start of transition configuration";
}
