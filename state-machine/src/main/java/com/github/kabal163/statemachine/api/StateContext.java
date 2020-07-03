package com.github.kabal163.statemachine.api;

import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * Contains information about the transition. It helps to share
 * information between actions and conditions.
 */
@RequiredArgsConstructor
public class StateContext {

    private final StatefulObject statefulObject;
    private final String event;
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

    public String getEvent() {
        return event;
    }
}
