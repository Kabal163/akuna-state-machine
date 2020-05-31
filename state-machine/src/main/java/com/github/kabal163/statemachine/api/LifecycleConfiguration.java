package com.github.kabal163.statemachine.api;

import com.github.kabal163.statemachine.TransitionConfigurer;

public interface LifecycleConfiguration<S, E> {

    void configureTransitions(TransitionConfigurer<S, E> configurer);
}
