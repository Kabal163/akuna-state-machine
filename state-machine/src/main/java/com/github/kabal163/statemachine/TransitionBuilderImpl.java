package com.github.kabal163.statemachine;

import com.github.kabal163.statemachine.api.Action;
import com.github.kabal163.statemachine.api.Condition;
import com.github.kabal163.statemachine.api.StatefulObject;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.ObjectUtils.allNotNull;

public class TransitionBuilderImpl<S, E> implements TransitionBuilder<S, E> {

    private final Set<TempTransition<S, E>> tmpTransitions = new HashSet<>();
    private TempTransition<S, E> currentlyConfigured;

    @Override
    public TransitionConfigurer<S, E> with() {
        currentlyConfigured = new TempTransition<>();
        tmpTransitions.add(currentlyConfigured);

        return this;
    }

    @Override
    public TransitionConfigurer<S, E> sourceState(S state) {
        Objects.requireNonNull(state, "sourceState must not be null!");
        checkConfiguredTransitionIsNotNull();

        currentlyConfigured.setSourceState(state);

        return this;
    }

    @Override
    public TransitionConfigurer<S, E> targetState(S state) {
        Objects.requireNonNull(state, "targetState must not be null!");
        checkConfiguredTransitionIsNotNull();

        currentlyConfigured.setTargetState(state);

        return this;
    }

    @Override
    public TransitionConfigurer<S, E> event(E event) {
        Objects.requireNonNull(event, "event must not be null!");
        checkConfiguredTransitionIsNotNull();

        currentlyConfigured.setEvent(event);

        return this;
    }

    @Override
    public TransitionConfigurer<S, E> condition(Condition<S, E> condition) {
        Objects.requireNonNull(condition, "Condition must not be null!");
        checkConfiguredTransitionIsNotNull();

        currentlyConfigured.addCondition(condition);

        return this;
    }

    @Override
    public TransitionConfigurer<S, E> action(Action<S, E> action) {
        Objects.requireNonNull(action, "Action must not be null!");
        checkConfiguredTransitionIsNotNull();

        currentlyConfigured.addAction(action);

        return this;
    }

    @Override
    public Set<Transition<S, E>> buildTransitions() {
        for (TempTransition<S, E> transition : tmpTransitions) {
            if (!allNotNull(
                    transition.getSourceState(),
                    transition.getTargetState(),
                    transition.getEvent())) {
                throw new IllegalStateException("Transition must have mandatory parameters - sourceState, targetState, event." +
                        "But something is null for transition: " + transition.getClass().getName());
            }
        }

        return tmpTransitions.stream()
                .map(tmp -> new Transition<>(
                        tmp.getSourceState(),
                        tmp.getTargetState(),
                        tmp.getEvent(),
                        tmp.getConditions(),
                        tmp.getActions()))
                .collect(toSet());
    }

    private void checkConfiguredTransitionIsNotNull() {
        if (currentlyConfigured == null) {
            throw new IllegalStateException(ERROR_MESSAGE);
        }
    }

    /**
     * Helper class used as temporary transition's data holder.
     *
     * @param <S> type of the state of the {@link StatefulObject stateful object}
     * @param <E> type of event
     */
    private static final class TempTransition<S, E> {
        private S sourceState;
        private S targetState;
        private E event;
        private final Set<Condition<S, E>> conditions = new HashSet<>();
        private final List<Action<S, E>> actions = new LinkedList<>();

        public S getSourceState() {
            return sourceState;
        }

        public void setSourceState(S sourceState) {
            this.sourceState = sourceState;
        }

        public S getTargetState() {
            return targetState;
        }

        public void setTargetState(S targetState) {
            this.targetState = targetState;
        }

        public E getEvent() {
            return event;
        }

        public void setEvent(E event) {
            this.event = event;
        }

        public Set<Condition<S, E>> getConditions() {
            return conditions;
        }

        public void addCondition(Condition<S, E> condition) {
            this.conditions.add(condition);
        }

        public List<Action<S, E>> getActions() {
            return actions;
        }

        public void addAction(Action<S, E> action) {
            this.actions.add(action);
        }
    }

    private static final String ERROR_MESSAGE = "Bad usage of a TransitionConfigurer. Probably you forget about with() method in start of transition configuration";
}
