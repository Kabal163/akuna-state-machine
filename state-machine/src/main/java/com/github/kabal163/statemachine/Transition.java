package com.github.kabal163.statemachine;

import com.github.kabal163.statemachine.api.Action;
import com.github.kabal163.statemachine.api.Condition;
import com.github.kabal163.statemachine.api.StateContext;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Data
class Transition<S, E> {

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
}
