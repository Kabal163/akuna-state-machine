package io.github.kabal163.statemachine.api;

public interface Action<S, E> {

    void execute(StateContext<S, E> context);
}
