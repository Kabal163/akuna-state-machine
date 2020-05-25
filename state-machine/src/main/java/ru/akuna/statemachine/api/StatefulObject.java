package ru.akuna.statemachine.api;

public interface StatefulObject<S> {

    String getId();

    S getState();

    void setState(S state);
}
