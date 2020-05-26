package io.github.kabal163.statemachine.api;

public interface StatefulObject<S> {

    String getId();

    S getState();

    void setState(S state);
}
