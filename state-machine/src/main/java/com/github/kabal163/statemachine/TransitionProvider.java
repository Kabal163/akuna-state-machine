package com.github.kabal163.statemachine;

import com.github.kabal163.statemachine.api.StatefulObject;
import com.github.kabal163.statemachine.exception.AmbiguousTransitionException;
import com.github.kabal163.statemachine.exception.TransitionNotFoundException;

public interface TransitionProvider {

    /**
     *
     * @param statefulObject
     * @param event
     * @return
     * @throws AmbiguousTransitionException if more then one matching transition is found
     * @throws TransitionNotFoundException if no matching transition is found
     */
    Transition getTransition(StatefulObject statefulObject, String event);
}
