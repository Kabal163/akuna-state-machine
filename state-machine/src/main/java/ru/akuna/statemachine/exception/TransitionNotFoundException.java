package ru.akuna.statemachine.exception;

public class TransitionNotFoundException extends TransitionException {

    public TransitionNotFoundException() {
    }

    public TransitionNotFoundException(String message) {
        super(message);
    }
}
