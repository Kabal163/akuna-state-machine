package ru.akuna.statemachine.api;

public interface Action<S, E> {

    void execute(StateContext<S, E> context);
}
