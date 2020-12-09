package com.github.kabal163.statemachine;

import com.github.kabal163.statemachine.api.Action;
import com.github.kabal163.statemachine.api.Condition;
import com.github.kabal163.statemachine.api.StateContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Transition<S, E> {

    private final S sourceState;
    private final S targetState;
    private final E event;

    private final Set<Condition<S, E>> conditions;
    private final List<Action<S, E>> actions;

    public Transition(S sourceState,
                      S targetState,
                      E event,
                      Set<Condition<S, E>> conditions,
                      List<Action<S, E>> actions) {
        Objects.requireNonNull(sourceState, "sourceState must not be null!");
        Objects.requireNonNull(targetState, "targetState must not be null!");
        Objects.requireNonNull(event, "event must not be null!");
        Objects.requireNonNull(conditions, "conditions must not be null!");
        Objects.requireNonNull(actions, "actions must not be null!");

        this.sourceState = sourceState;
        this.targetState = targetState;
        this.event = event;
        this.conditions = conditions;
        this.actions = actions;
    }

    public boolean transit(StateContext<S, E> context) {
        if (!conditions.stream().allMatch(condition -> condition.evaluate(context))) {
            return false;
        }
        actions.forEach(a -> a.execute(context));

        return true;
    }

    public Set<Condition<S, E>> getConditions() {
        return new HashSet<>(conditions);
    }

    public List<Action<S, E>> getActions() {
        return new ArrayList<>(actions);
    }

    public S getSourceState() {
        return sourceState;
    }

    public S getTargetState() {
        return targetState;
    }

    public E getEvent() {
        return event;
    }
}
