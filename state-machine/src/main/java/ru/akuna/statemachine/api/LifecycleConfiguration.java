package ru.akuna.statemachine.api;

import ru.akuna.statemachine.TransitionConfigurer;

public interface LifecycleConfiguration<S, E> {

    void configureTransitions(TransitionConfigurer<S, E> configurer);
}
