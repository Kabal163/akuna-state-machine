package ru.akuna.statemachine.api;

public interface Condition<S, E> {

    boolean evaluate(StateContext<S, E> context);
}
