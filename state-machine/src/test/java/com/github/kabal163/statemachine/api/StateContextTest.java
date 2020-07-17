package com.github.kabal163.statemachine.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StateContextTest {

    @Mock
    StatefulObject statefulObject;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void givenContextCreatedWithEmptyMapWhenPutVariableThenValueMustBeInserted() {
        StateContext context = new StateContext(statefulObject, "anyEvent", emptyMap());
        context.putVariable("key", "value");

        assertEquals("value", context.getVariable("key", String.class));
    }

    @Test
    void givenContextCreatedWithEmptyMapWhenPutVariableIfAbsentThenValueMustBeInserted() {
        StateContext context = new StateContext(statefulObject, "anyEvent", emptyMap());
        context.putIfAbsentVariable("key", "value");

        assertEquals("value", context.getVariable("key", String.class));
    }

    @Test
    void givenKeyDoesntExistInContextWhenPutVariableIfAbsentThenValueMustBeInserted() {
        StateContext context = new StateContext(statefulObject, "anyEvent", new HashMap<>());
        context.putIfAbsentVariable("key", "value");

        assertEquals("value", context.getVariable("key", String.class));
    }

    @Test
    void givenKeyAlreadyExistsInContextWhenPutVariableIfAbsentThenValueMustNotBeInserted() {
        StateContext context = new StateContext(statefulObject, "anyEvent", singletonMap("key", "value"));
        context.putIfAbsentVariable("key", "value1");

        assertEquals("value", context.getVariable("key", String.class));
    }

    @Test
    void whenGetEventThenMustReturnEvent() {
        StateContext context = new StateContext(statefulObject, "anyEvent", emptyMap());
        String event = context.getEvent();

        assertEquals("anyEvent", event);
    }

    @Test
    void givenValueIsStringTypeWhenGetVariableWithStringTypeThenReturnsValue() {
        StateContext context = new StateContext(statefulObject, "anyEvent", singletonMap("key", "value"));
        String value = context.getVariable("key", String.class);

        assertEquals(String.class, value.getClass());
    }

    @Test
    void givenValueIsStringTypeWhenGetVariableWithIntegerTypeThenThrowsIllegalArgumentException() {
        StateContext context = new StateContext(statefulObject, "anyEvent", singletonMap("key", "value"));
        assertThrows(IllegalArgumentException.class, () -> context.getVariable("key", Integer.class));
    }
}