package io.github.kabal163.statemachine;

import io.github.kabal163.statemachine.api.Action;
import io.github.kabal163.statemachine.api.Condition;

public interface TransitionConfigurer<S, E> {

    TransitionConfigurer<S, E> with();

    TransitionConfigurer<S, E> sourceState(S state);

    TransitionConfigurer<S, E> targetState(S state);

    TransitionConfigurer<S, E> event(E event);

    TransitionConfigurer<S, E> condition(Condition<S, E> condition);

    TransitionConfigurer<S, E> action(Action<S, E> action);
}
