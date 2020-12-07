package com.github.kabal163.statemachine;

import com.github.kabal163.statemachine.api.Action;
import com.github.kabal163.statemachine.api.Condition;
import com.github.kabal163.statemachine.api.StateContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Transition<S, E> {

    private S sourceState;
    private S targetState;
    private E event;

    private Set<Condition<S, E>> conditions = new HashSet<>();
    private List<Action<S, E>> actions = new LinkedList<>();

    public boolean transit(StateContext<S, E> context) {
        if (!conditions.stream().allMatch(condition -> condition.evaluate(context))) {
            return false;
        }
        actions.forEach(a -> a.execute(context));

        return true;
    }

    public void addAction(Action<S, E> action) {
        actions.add(action);
    }

    public void addCondition(Condition<S, E> condition) {
        conditions.add(condition);
    }

    public Collection<Condition<S, E>> getConditions() {
        return new HashSet<>(conditions);
    }

    public Collection<Action<S, E>> getActions() {
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

    public void setSourceState(S sourceState) {
        this.sourceState = sourceState;
    }

    public void setTargetState(S targetState) {
        this.targetState = targetState;
    }

    public void setEvent(E event) {
        this.event = event;
    }
}
