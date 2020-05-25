package ru.akuna.statemachine;

import java.util.Set;

interface TransitionBuilder<S, E> extends TransitionConfigurer<S, E> {

    Set<Transition<S, E>> buildTransitions();
}
