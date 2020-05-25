package ru.akuna.statemachine.api;

import java.util.Map;

public interface LifecycleManager<S, E> {

    TransitionResult<S, E> execute(StatefulObject<S> statefulObject, E event);

    TransitionResult<S, E> execute(StatefulObject<S> statefulObject, E event, Map<String, Object> variables);
}
