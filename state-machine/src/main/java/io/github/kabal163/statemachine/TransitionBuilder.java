package io.github.kabal163.statemachine;

import java.util.Set;

interface TransitionBuilder<S, E> extends TransitionConfigurer<S, E> {

    Set<Transition<S, E>> buildTransitions();
}
