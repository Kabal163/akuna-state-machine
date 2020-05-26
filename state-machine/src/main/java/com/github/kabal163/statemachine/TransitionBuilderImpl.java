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
        configuredTransition.setSourceState(state);

        return this;
    }

    @Override
    public TransitionConfigurer<S, E> targetState(S state) {
        configuredTransition.setTargetState(state);

        return this;
    }

    @Override
    public TransitionConfigurer<S, E> event(E event) {
        configuredTransition.setEvent(event);

        return this;
    }

    @Override
    public TransitionConfigurer<S, E> condition(Condition<S, E> condition) {
        if (condition == null) {
            throw new IllegalArgumentException("Condition must not be null!");
        }
        configuredTransition.addCondition(condition);

        return this;
    }

    @Override
    public TransitionConfigurer<S, E> action(Action<S, E> action) {
        if (action == null) {
            throw new IllegalArgumentException("Action must not be null!");
        }
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
}
