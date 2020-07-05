package com.github.kabal163.statemachine;

import com.github.kabal163.statemachine.api.LifecycleConfiguration;
import com.github.kabal163.statemachine.api.StateContext;
import com.github.kabal163.statemachine.api.StatefulObject;
import com.github.kabal163.statemachine.api.TransitionResult;
import com.github.kabal163.statemachine.exception.AmbiguousTransitionException;
import com.github.kabal163.statemachine.exception.TransitionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

class LifecycleManagerImplTest {

    LifecycleManagerImpl lifecycleManager;

    LifecycleConfiguration lifecycleConfiguration;
    TransitionBuilder transitionBuilder;
    Transition approveTransition;
    Transition cancelTransition;

    LifecycleManagerImplTest() {
        lifecycleConfiguration = Mockito.mock(LifecycleConfiguration.class);
        transitionBuilder = Mockito.mock(TransitionBuilder.class);
        approveTransition = Mockito.mock(Transition.class);
        cancelTransition = Mockito.mock(Transition.class);
    }

    @BeforeEach
    void setUp() {
        Mockito.when(approveTransition.getSourceState()).thenReturn("NEW");
        Mockito.when(approveTransition.getTargetState()).thenReturn("APPROVED");
        Mockito.when(approveTransition.getEvent()).thenReturn("APPROVE");
        Mockito.when(approveTransition.transit(any())).thenReturn(true);

        Mockito.when(cancelTransition.getSourceState()).thenReturn("NEW");
        Mockito.when(cancelTransition.getTargetState()).thenReturn("CANCELED");
        Mockito.when(cancelTransition.getEvent()).thenReturn("CANCEL");
        Mockito.when(cancelTransition.transit(any())).thenReturn(true);

        Mockito.when(transitionBuilder.buildTransitions()).thenReturn(Set.of(approveTransition, cancelTransition));

        lifecycleManager = new LifecycleManagerImpl(transitionBuilder, lifecycleConfiguration);
        lifecycleManager.init();
    }

    @ParameterizedTest
    @MethodSource("getValidStatefulObjects")
    void transitionMustBeFound(StatefulObject statefulObject, String event) {
        TransitionResult result = lifecycleManager.execute(statefulObject, event);
        assertTrue(result.isSuccess());
    }

    @Test
    void approvedTransitionMustBeUsed() {
        StatefulObject statefulObject = Mockito.mock(StatefulObject.class);
        Mockito.when(statefulObject.getState()).thenReturn("NEW");
        Mockito.doNothing().when(statefulObject).setState(any());

        lifecycleManager.execute(statefulObject, "APPROVE");

        Mockito.verify(approveTransition, Mockito.times(1)).transit(any());
        Mockito.verify(cancelTransition, Mockito.times(0)).transit(any());
    }

    @Test
    void cancelTransitionMustBeUsed() {
        StatefulObject statefulObject = Mockito.mock(StatefulObject.class);
        Mockito.when(statefulObject.getState()).thenReturn("NEW");
        Mockito.doNothing().when(statefulObject).setState(any());

        lifecycleManager.execute(statefulObject, "CANCEL");

        Mockito.verify(approveTransition, Mockito.times(0)).transit(any());
        Mockito.verify(cancelTransition, Mockito.times(1)).transit(any());
    }

    @Test
    void contextMustContainStatefulObject() {
        StatefulObject statefulObject = Mockito.mock(StatefulObject.class);
        Mockito.when(statefulObject.getState()).thenReturn("NEW");
        Mockito.doNothing().when(statefulObject).setState(any());

        ArgumentCaptor<StateContext> contextCaptor = ArgumentCaptor.forClass(StateContext.class);
        Mockito.when(approveTransition.transit(contextCaptor.capture())).thenReturn(true);

        lifecycleManager.execute(statefulObject, "APPROVE");
        StateContext context = contextCaptor.getValue();

        assertEquals(statefulObject, context.getStatefulObject());
    }

    @Test
    void contextMustContainEvent() {
        StatefulObject statefulObject = Mockito.mock(StatefulObject.class);
        Mockito.when(statefulObject.getState()).thenReturn("NEW");
        Mockito.doNothing().when(statefulObject).setState(any());

        ArgumentCaptor<StateContext> contextCaptor = ArgumentCaptor.forClass(StateContext.class);
        Mockito.when(approveTransition.transit(contextCaptor.capture())).thenReturn(true);

        lifecycleManager.execute(statefulObject, "APPROVE");
        StateContext context = contextCaptor.getValue();

        assertEquals("APPROVE", context.getEvent());
    }

    @Test
    void contextMustContainVariables() {
        StatefulObject statefulObject = Mockito.mock(StatefulObject.class);
        Mockito.when(statefulObject.getState()).thenReturn("NEW");
        Mockito.doNothing().when(statefulObject).setState(any());

        ArgumentCaptor<StateContext> contextCaptor = ArgumentCaptor.forClass(StateContext.class);
        Mockito.when(approveTransition.transit(contextCaptor.capture())).thenReturn(true);
        Map<String, Object> variables = singletonMap("testKey", "testValue");

        lifecycleManager.execute(statefulObject, "APPROVE", variables);
        StateContext context = contextCaptor.getValue();

        assertEquals("testValue", context.getVariable("testKey", String.class));
    }

    @Test
    void contextVariablesMustBeAppendable() {
        StatefulObject statefulObject = Mockito.mock(StatefulObject.class);
        Mockito.when(statefulObject.getState()).thenReturn("NEW");
        Mockito.doNothing().when(statefulObject).setState(any());

        ArgumentCaptor<StateContext> contextCaptor = ArgumentCaptor.forClass(StateContext.class);
        Mockito.when(approveTransition.transit(contextCaptor.capture())).thenReturn(true);

        lifecycleManager.execute(statefulObject, "APPROVE");
        StateContext context = contextCaptor.getValue();
        context.putVariable("testKey", "testValue");

        assertEquals("testValue", context.getVariable("testKey", String.class));
    }

    @Test
    void exceptionDueToAmbiguousTransition() {
        Mockito.when(cancelTransition.getEvent()).thenReturn("APPROVE");

        StatefulObject statefulObject = Mockito.mock(StatefulObject.class);
        Mockito.when(statefulObject.getState()).thenReturn("NEW");
        Mockito.doNothing().when(statefulObject).setState(any());

        assertThrows(AmbiguousTransitionException.class, () -> lifecycleManager.execute(statefulObject, "APPROVE"));
    }

    @Test
    void exceptionDueToNoSuchTransition() {
        StatefulObject statefulObject = Mockito.mock(StatefulObject.class);
        Mockito.when(statefulObject.getState()).thenReturn("APPROVED");
        Mockito.doNothing().when(statefulObject).setState(any());

        assertThrows(TransitionNotFoundException.class, () -> lifecycleManager.execute(statefulObject, "APPROVE"));
    }

    @Test
    void transitionResultContainsExceptionIfItHappened() {
        Mockito.when(approveTransition.transit(any())).thenThrow(RuntimeException.class);

        StatefulObject statefulObject = Mockito.mock(StatefulObject.class);
        Mockito.when(statefulObject.getState()).thenReturn("NEW");
        Mockito.doNothing().when(statefulObject).setState(any());

        TransitionResult result = lifecycleManager.execute(statefulObject, "APPROVE");

        assertNotNull(result.getException());
        assertEquals(RuntimeException.class, result.getException().getClass());
    }

    @Test
    void transitionResultIsNotSuccessfulIfTransitionReturnsFalse() {
        Mockito.when(approveTransition.transit(any())).thenReturn(false);

        StatefulObject statefulObject = Mockito.mock(StatefulObject.class);
        Mockito.when(statefulObject.getState()).thenReturn("NEW");
        Mockito.doNothing().when(statefulObject).setState(any());

        TransitionResult result = lifecycleManager.execute(statefulObject, "APPROVE");

        assertFalse(result.isSuccess());
    }

    @Test
    void transitionResultIsNotSuccessfulIfTransitionThrowsException() {
        Mockito.when(approveTransition.transit(any())).thenThrow(RuntimeException.class);

        StatefulObject statefulObject = Mockito.mock(StatefulObject.class);
        Mockito.when(statefulObject.getState()).thenReturn("NEW");
        Mockito.doNothing().when(statefulObject).setState(any());

        TransitionResult result = lifecycleManager.execute(statefulObject, "APPROVE");

        assertFalse(result.isSuccess());
    }

    @Test
    void stateMustBeChangedOnTargetIfTransitionIsSuccessful() {
        ArgumentCaptor<String> stateCaptor = ArgumentCaptor.forClass(String.class);
        StatefulObject statefulObject = Mockito.mock(StatefulObject.class);
        Mockito.when(statefulObject.getState()).thenReturn("NEW");
        Mockito.doNothing().when(statefulObject).setState(stateCaptor.capture());

        lifecycleManager.execute(statefulObject, "APPROVE");

        String actualTargetState = stateCaptor.getValue();
        String expectedTargetState = approveTransition.getTargetState();

        assertEquals(expectedTargetState, actualTargetState);
    }

    @Test
    void stateMustNotBeChangedIfTransitionReturnsFalse() {
        Mockito.when(approveTransition.transit(any())).thenReturn(false);

        StatefulObject statefulObject = Mockito.mock(StatefulObject.class);
        Mockito.when(statefulObject.getState()).thenReturn("NEW");
        Mockito.doNothing().when(statefulObject).setState(any());

        lifecycleManager.execute(statefulObject, "APPROVE");

        Mockito.verify(statefulObject, Mockito.times(0)).setState(approveTransition.getTargetState());
    }

    @Test
    void stateMustNotBeChangedIfTransitionThrowsException() {
        Mockito.when(approveTransition.transit(any())).thenThrow(RuntimeException.class);

        StatefulObject statefulObject = Mockito.mock(StatefulObject.class);
        Mockito.when(statefulObject.getState()).thenReturn("NEW");
        Mockito.doNothing().when(statefulObject).setState(any());

        lifecycleManager.execute(statefulObject, "APPROVE");

        Mockito.verify(statefulObject, Mockito.times(0)).setState(approveTransition.getTargetState());
    }

    public static Stream<Arguments> getValidStatefulObjects() {
        StatefulObject o1 = Mockito.mock(StatefulObject.class);
        StatefulObject o2 = Mockito.mock(StatefulObject.class);

        Mockito.when(o1.getState()).thenReturn("NEW");
        Mockito.doNothing().when(o1).setState(any());
        Mockito.when(o2.getState()).thenReturn("NEW");
        Mockito.doNothing().when(o2).setState(any());

        return Stream.of(
                Arguments.of(o1, "APPROVE"),
                Arguments.of(o1, "CANCEL")
        );
    }
}