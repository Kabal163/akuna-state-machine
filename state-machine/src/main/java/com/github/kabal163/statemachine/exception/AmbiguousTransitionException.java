package com.github.kabal163.statemachine.exception;

public class AmbiguousTransitionException extends TransitionException {

    public AmbiguousTransitionException(String message) {
        super(message);
    }
}
