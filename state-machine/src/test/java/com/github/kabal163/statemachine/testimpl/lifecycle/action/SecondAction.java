package com.github.kabal163.statemachine.testimpl.lifecycle.action;

import com.github.kabal163.statemachine.api.Action;
import com.github.kabal163.statemachine.api.StateContext;
import com.github.kabal163.statemachine.testimpl.Event;
import com.github.kabal163.statemachine.testimpl.State;

public class SecondAction implements Action<State, Event> {

    private static final String KEY = "secondAction";
    private static final String VALUE = "secondAction";

    @Override
    public void execute(StateContext<State, Event> context) {
        context.putVariable(KEY, VALUE);
    }
}
