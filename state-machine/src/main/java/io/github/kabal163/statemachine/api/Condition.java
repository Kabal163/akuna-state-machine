package io.github.kabal163.statemachine.api;

public interface Condition<S, E> {

    boolean evaluate(StateContext<S, E> context);
}
