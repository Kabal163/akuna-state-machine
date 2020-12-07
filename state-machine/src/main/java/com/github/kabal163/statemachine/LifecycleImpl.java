package com.github.kabal163.statemachine;

import java.util.Objects;
import java.util.Set;

public class LifecycleImpl<S, E> implements Lifecycle<S, E> {

    private final String name;
    private final Set<Transition<S, E>> transitions;

    private LifecycleImpl(String name, Set<Transition<S, E>> transitions) {
        this.name = name;
        this.transitions = transitions;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<Transition<S, E>> getTransitions() {
        return transitions;
    }

    public static <S, E> Builder<S, E> builder() {
        return new Builder<>();
    }

    public static final class Builder<S, E> {
        private String name;
        private Set<Transition<S, E>> transitions;

        public Builder<S, E> name(String name) {
            this.name = name;
            return this;
        }

        public Builder<S, E> transitions(Set<Transition<S, E>> transitions) {
            this.transitions = transitions;
            return this;
        }

        public Lifecycle<S, E> build() {
            Objects.requireNonNull(name, "Lifecycle's name must not be blank!");
            Objects.requireNonNull(name, "transitions must not be null!");

            return new LifecycleImpl<>(name, transitions);
        }
    }
}
