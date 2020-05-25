package ru.akuna.statemachine;

import lombok.Data;
import ru.akuna.statemachine.api.StateContext;
import ru.akuna.statemachine.api.Action;
import ru.akuna.statemachine.api.Condition;

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
}
