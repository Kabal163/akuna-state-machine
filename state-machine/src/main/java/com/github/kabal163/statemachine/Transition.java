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
public class Transition {

    private String sourceState;
    private String targetState;
    private String event;

    private Set<Condition> conditions = new HashSet<>();
    private List<Action> actions = new LinkedList<>();

    public boolean transit(StateContext context) {
        if (!conditions.stream().allMatch(condition -> condition.evaluate(context))) {
            return false;
        }
        actions.forEach(a -> a.execute(context));

        return true;
    }

    public void addAction(Action action) {
        actions.add(action);
    }

    public void addCondition(Condition condition) {
        conditions.add(condition);
    }

    public Collection<Condition> getConditions() {
        return new HashSet<>(conditions);
    }

    public Collection<Action> getActions() {
        return new ArrayList<>(actions);
    }
}
