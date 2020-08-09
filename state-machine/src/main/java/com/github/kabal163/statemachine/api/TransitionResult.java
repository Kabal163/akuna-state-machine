package com.github.kabal163.statemachine.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Contains the main information about executed transition.
 * If there were no exceptions then {@code exception} field
 * will be {@code null}.
 */
@Getter
@RequiredArgsConstructor
public class TransitionResult {

    /**
     * Whether the transition was successful or not
     */
    private final boolean succeeded;

    /**
     * The shared context of the transition
     */
    private final StateContext stateContext;

    /**
     * The state which stateful object has at the moment
     * of start the transition
     */
    private final String sourceState;

    /**
     * The state which stateful object should has at the moment
     * of finish the transition. This is the desired state which may
     * doesn't match the actual state of the stateful object after
     * transition execution due to exceptions.
     */
    private final String targetState;

    /**
     * Any exception which happened during actions or conditions execution
     * {@code null} if nothing happened
     */
    private final Exception exception;
}
