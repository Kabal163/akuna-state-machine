package com.github.kabal163.statemachine.testimpl.lifecycle.action;

import com.github.kabal163.statemachine.api.Action;
import com.github.kabal163.statemachine.api.StateContext;
import com.github.kabal163.statemachine.testimpl.Event;
import com.github.kabal163.statemachine.testimpl.State;

public class FirstAction implements Action<State, Event> {

    private static final String KEY = "firstAction";
    private static final String VALUE = "firstAction";

    @Override
    public void execute(StateContext<State, Event> context) {
        context.putVariable(KEY, VALUE);
    }
}
