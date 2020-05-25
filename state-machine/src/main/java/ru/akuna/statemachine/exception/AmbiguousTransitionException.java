package ru.akuna.statemachine.exception;

public class AmbiguousTransitionException extends TransitionException {

    public AmbiguousTransitionException() {
    }

    public AmbiguousTransitionException(String message) {
        super(message);
    }
}
