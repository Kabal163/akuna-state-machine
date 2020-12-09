package com.github.kabal163.statemachine.api;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Contains information about the transition. It helps to share
 * information between actions and conditions.
 *
 * @param <S> type of the state of the {@link StatefulObject stateful object}
 * @param <E> type of event
 */
public class StateContext<S, E> {

    private final StatefulObject<S> statefulObject;
    private final E event;
    private final Map<String, Object> variables;

    public StateContext(StatefulObject<S> statefulObject,
                        E event,
                        Map<String, Object> variables) {
        Objects.requireNonNull(statefulObject, "StatefulObject must not be null!");
        Objects.requireNonNull(event, "event must not be null!");
        Objects.requireNonNull(variables, "variables must not be null!");

        this.statefulObject = statefulObject;
        this.event = event;
        this.variables = new HashMap<>(variables);
    }

    public void putVariable(String key, Object value) {
        variables.put(key, value);
    }

    public void putIfAbsentVariable(String key, Object value) {
        variables.putIfAbsent(key, value);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public  <T> T getVariable(String key, Class<T> type) {
        Object value = this.variables.get(key);
        if (value == null) {
            return null;
        }
        if (!type.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("Incorrect type specified for variable '" +
                    key + "'. Expected [" + type + "] but actual type is [" + value.getClass() + "]");
        }
        return (T) value;
    }

    @SuppressWarnings("unchecked")
    public <T extends StatefulObject<S>> T getStatefulObject() {
        return (T) statefulObject;
    }

    public E getEvent() {
        return event;
    }
}
