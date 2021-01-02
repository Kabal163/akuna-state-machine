package com.github.kabal163.statemachine.api;

import com.github.kabal163.statemachine.exception.ContextVariableNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.github.kabal163.statemachine.TestEvent;
import com.github.kabal163.statemachine.TestState;

import java.util.HashMap;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static com.github.kabal163.statemachine.TestEvent.EVENT;

class StateContextTest {

    @Mock
    StatefulObject<TestState> statefulObject;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Given StateContext is created with empty map " +
            "When call StateContext.putVariable " +
            "Then variable must be inserted")
    void givenStateContextCreatedWithEmptyMap_whenCallPutVariable_thenValueMustBeInserted() {
        final String expected = "value";
        StateContext<TestState, TestEvent> context = new StateContext<>(statefulObject, EVENT, emptyMap());
        context.putVariable("key", "value");
        String actual = context.getVariable("key", String.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Given StateContext is created with empty map " +
            "When call StateContext.putIfAbsentVariable " +
            "Then variable must be inserted")
    void givenStateContextCreatedWithEmptyMap_whenCallPutVariableIfAbsent_thenValueMustBeInserted() {
        final String expected = "value";
        StateContext<TestState, TestEvent> context = new StateContext<>(statefulObject, EVENT, emptyMap());
        context.putIfAbsentVariable("key", expected);
        String actual = context.getVariable("key", String.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Given key doesn't exist in StateContext " +
            "When call StateContext.putIfAbsentVariable " +
            "Then variable must be inserted")
    void givenKeyDoesntExistInStateContext_whenCallPutVariableIfAbsent_thenValueMustBeInserted() {
        final String expected = "value";
        StateContext<TestState, TestEvent> context = new StateContext<>(statefulObject, EVENT, new HashMap<>());
        context.putIfAbsentVariable("key", expected);
        String actual = context.getVariable("key", String.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Given key already exists in StateContext " +
            "When call StateContext.putIfAbsentVariable " +
            "Then variable must not be inserted")
    void givenKeyAlreadyExistsInContext_whenCallPutVariableIfAbsent_thenValueMustNotBeInserted() {
        final String expected = "value";
        StateContext<TestState, TestEvent> context = new StateContext<>(statefulObject, EVENT, singletonMap("key", expected));
        context.putIfAbsentVariable("key", "value1");
        String actual = context.getVariable("key", String.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("When call StateContext.getEvent " +
            "Then must return the event")
    void whenCallGetEvent_thenMustReturnTheEvent() {
        final TestEvent expected = EVENT;
        StateContext<TestState, TestEvent> context = new StateContext<>(statefulObject, expected, emptyMap());
        TestEvent actual = context.getEvent();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Given value of string type " +
            "When call StateContext.getVariable with String.class as argument" +
            "Then returns value of string type")
    void givenValueOfStringType_whenCallGetVariableWithStringType_thenReturnsValueOfStringType() {
        StateContext<TestState, TestEvent> context = new StateContext<>(statefulObject, EVENT, singletonMap("key", "value"));
        String actual = context.getVariable("key", String.class);

        assertThat(actual.getClass()).isEqualTo(String.class);
    }

    @Test
    @DisplayName("Given of string type " +
            "When call StateContext.getVariable with Integer.class as argument " +
            "Then throws IllegalArgumentException")
    void givenValueOfStringType_whenCallGetVariableWithIntegerType_thenThrowsIllegalArgumentException() {
        StateContext<TestState, TestEvent> context = new StateContext(statefulObject, EVENT, singletonMap("key", "value"));
        assertThatThrownBy(() -> context.getVariable("key", Integer.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Incorrect type specified for variable");
    }

    @Test
    @DisplayName("Given key doesn't exist in StateContext " +
            "When call getVariableOrElseThrow(String, Class) " +
            "Then throws ContextVariableNotFoundException")
    void givenKeyDoesntExistInStateContext_whenCallGetVariableOrElseThrow_thenThrowsContextVariableNotFoundException_1() {
        StateContext<TestState, TestEvent> context = new StateContext<>(statefulObject, EVENT, new HashMap<>());
        assertThatThrownBy(() -> context.getVariableOrElseThrow("nonExistent", String.class))
                .isInstanceOf(ContextVariableNotFoundException.class)
                .hasMessageContaining("Expected that StateContext contains a key but it doesn't! The key: ");
    }

    @Test
    @DisplayName("Given key doesn't exist in StateContext " +
            "When call getVariableOrElseThrow(String, Class, RuntimeException) with ContextVariableNotFoundException " +
            "Then throws ContextVariableNotFoundException")
    void givenKeyDoesntExistInStateContext_whenCallGetVariableOrElseThrow_thenThrowsContextVariableNotFoundException_2() {
        final RuntimeException expected = new ContextVariableNotFoundException("My custom exception");
        StateContext<TestState, TestEvent> context = new StateContext<>(statefulObject, EVENT, new HashMap<>());
        assertThatThrownBy(() -> context.getVariableOrElseThrow("nonExistent", String.class, expected))
                .isInstanceOf(expected.getClass())
                .hasMessageContaining(expected.getMessage());
    }
}