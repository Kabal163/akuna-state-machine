package com.github.kabal163.statemachine.api;

import javax.annotation.Nullable;

/**
 * Contains the main information about executed transition.
 * If there were no exceptions then {@code exception} field
 * will be {@code null}.
 *
 * @param <S> type of the state of the {@link StatefulObject stateful object}
 * @param <E> type of event
 */
public class TransitionResult<S, E> {

    /**
     * Whether the transition was successful or not
     */
    private final boolean succeeded;

    /**
     * The shared context of the transition
     */
    private final StateContext<S, E> stateContext;

    /**
     * The state which stateful object has at the moment
     * of start the transition
     */
    private final S sourceState;

    /**
     * The state which stateful object should has at the moment
     * of finish the transition. This is the desired state which may
     * doesn't match the actual state of the stateful object after
     * transition execution due to exceptions.
     */
    private final S targetState;

    /**
     * Any exception which happened during actions or conditions execution
     * {@code null} if nothing happened
     */
    @Nullable
    private final Exception exception;

    public TransitionResult(boolean succeeded,
                            StateContext<S, E> stateContext,
                            S sourceState,
                            S targetState,
                            @Nullable Exception exception) {
        this.succeeded = succeeded;
        this.stateContext = stateContext;
        this.sourceState = sourceState;
        this.targetState = targetState;
        this.exception = exception;
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public StateContext<S, E> getStateContext() {
        return stateContext;
    }

    public S getSourceState() {
        return sourceState;
    }

    public S getTargetState() {
        return targetState;
    }

    @Nullable
    public Exception getException() {
        return exception;
    }
}
