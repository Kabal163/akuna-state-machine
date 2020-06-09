package com.github.kabal163.statemachine.api;

import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * Contains information about the transition. It helps to share
 * information between actions and conditions.
 *
 * @param <S> a state of a stateful object
 * @param <E> an event which triggers a transition
 */
@RequiredArgsConstructor
public class StateContext<S, E> {

    private final StatefulObject<S> statefulObject;
    private final E event;
    private final Map<String, Object> variables;

    public void putVariable(String key, Object value) {
        variables.put(key, value);
    }

    public void putIfAbsentVariable(String key, Object value) {
        variables.putIfAbsent(key, value);
    }

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
    public <T> T getStatefulObject() {
        return (T) statefulObject;
    }

    public E getEvent() {
        return event;
    }
}
