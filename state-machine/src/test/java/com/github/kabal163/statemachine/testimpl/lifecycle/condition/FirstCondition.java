package com.github.kabal163.statemachine.testimpl.lifecycle.condition;

import com.github.kabal163.statemachine.api.Condition;
import com.github.kabal163.statemachine.api.StateContext;
import com.github.kabal163.statemachine.testimpl.Event;
import com.github.kabal163.statemachine.testimpl.State;

public class FirstCondition implements Condition<State, Event> {

    private static final String KEY = "firstCondition";
    private static final String VALUE = "firstCondition";

    @Override
    public boolean evaluate(StateContext<State, Event> context) {
        context.putVariable(KEY, VALUE);
        return true;
    }
}
