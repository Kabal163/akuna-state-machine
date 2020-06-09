package com.github.kabal163.statemachine.api;

public interface StatefulObject<S> {

    <T> T getId();

    S getState();

    void setState(S state);
}
