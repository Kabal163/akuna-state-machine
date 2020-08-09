package com.github.kabal163.statemachine;

import com.github.kabal163.statemachine.api.StateContext;
import com.github.kabal163.statemachine.api.StatefulObject;
import com.github.kabal163.statemachine.api.TransitionResult;
import com.github.kabal163.statemachine.exception.AmbiguousTransitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LifecycleManagerImplTest {

    @Mock
    TransitionProvider transitionProvider;

    LifecycleManagerImpl lifecycleManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        lifecycleManager = new LifecycleManagerImpl(transitionProvider);
    }

    @Test
    void whenExecuteThenTransitionProvider_getTransitionIsCalled() {
        String event = "any";
        StatefulObject statefulObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(statefulObject, event)).thenReturn(transition);

        lifecycleManager.execute(statefulObject, event);

        verify(transitionProvider, times(1)).getTransition(statefulObject, event);
    }

    @Test
    void whenExecuteThenTransition_transitIsCalled() {
        String event = "any";
        StatefulObject statefulObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(statefulObject, event)).thenReturn(transition);

        lifecycleManager.execute(statefulObject, event);

        verify(transition, times(1)).transit(any(StateContext.class));
    }

    @Test
    void whenExecuteThenStateContextMustBePassedToTransition_transit() {
        String event = "any";
        StatefulObject statefulObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(statefulObject, event)).thenReturn(transition);
        ArgumentCaptor<StateContext> captor = ArgumentCaptor.forClass(StateContext.class);
        when(transition.transit(captor.capture())).thenReturn(true);

        lifecycleManager.execute(statefulObject, event);

        assertNotNull(captor.getValue());
    }

    @Test
    void whenExecuteThenStateContextMustContainStatefulObject() {
        String event = "any";
        StatefulObject expectedObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(expectedObject, event)).thenReturn(transition);
        ArgumentCaptor<StateContext> captor = ArgumentCaptor.forClass(StateContext.class);
        when(transition.transit(captor.capture())).thenReturn(true);

        lifecycleManager.execute(expectedObject, event);

        StatefulObject actualObject = captor.getValue().getStatefulObject();
        assertEquals(expectedObject, actualObject);
    }

    @Test
    void whenExecuteThenStateContextMustContainEvent() {
        String expectedEvent = "any";
        StatefulObject statefulObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(statefulObject, expectedEvent)).thenReturn(transition);
        ArgumentCaptor<StateContext> captor = ArgumentCaptor.forClass(StateContext.class);
        when(transition.transit(captor.capture())).thenReturn(true);

        lifecycleManager.execute(statefulObject, expectedEvent);

        String actualEvent = captor.getValue().getEvent();
        assertEquals(expectedEvent, actualEvent);
    }

    @Test
    void givenCustomVariablesWhenExecuteThenContextMustContainCustomVariables() {
        String event = "any";
        StatefulObject statefulObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(statefulObject, event)).thenReturn(transition);
        ArgumentCaptor<StateContext> captor = ArgumentCaptor.forClass(StateContext.class);
        when(transition.transit(captor.capture())).thenReturn(true);
        Map<String, Object> variables = singletonMap("expectedKey", "expectedValue");

        lifecycleManager.execute(statefulObject, event, variables);

        String actualValue = captor.getValue().getVariable("expectedKey", String.class);
        assertEquals("expectedValue", actualValue);
    }

    @Test
    void givenSuccessTransitionWhenExecuteThenReturnTransitionResult() {
        String event = "any";
        StatefulObject statefulObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(statefulObject, event)).thenReturn(transition);
        when(transition.transit(any(StateContext.class))).thenReturn(true);

        TransitionResult actualResult = lifecycleManager.execute(statefulObject, event);

        assertNotNull(actualResult);
    }

    @Test
    void givenFailedTransitionWhenExecuteThenReturnTransitionResult() {
        String event = "any";
        StatefulObject statefulObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(statefulObject, event)).thenReturn(transition);
        when(transition.transit(any(StateContext.class))).thenReturn(false);

        TransitionResult actualResult = lifecycleManager.execute(statefulObject, event);

        assertNotNull(actualResult);
    }

    @Test
    void givenTransitionThrowsExceptionWhenExecuteThenReturnTransitionResult() {
        String event = "any";
        StatefulObject statefulObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(statefulObject, event)).thenReturn(transition);
        doThrow(RuntimeException.class).when(transition).transit(any(StateContext.class));

        TransitionResult transitionResult = lifecycleManager.execute(statefulObject, event);

        assertNotNull(transitionResult);
    }

    @Test
    void givenSuccessTransitionWhenExecuteThenTransitionResultMustContainSucceededEqualsTrue() {
        String event = "any";
        StatefulObject statefulObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(statefulObject, event)).thenReturn(transition);
        when(transition.transit(any(StateContext.class))).thenReturn(true);

        TransitionResult transitionResult = lifecycleManager.execute(statefulObject, event);

        assertTrue(transitionResult.isSucceeded());
    }

    @Test
    void givenFailedTransitionWhenExecuteThenTransitionResultMustContainSucceededEqualsFalse() {
        String event = "any";
        StatefulObject statefulObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(statefulObject, event)).thenReturn(transition);
        when(transition.transit(any(StateContext.class))).thenReturn(false);

        TransitionResult transitionResult = lifecycleManager.execute(statefulObject, event);

        assertFalse(transitionResult.isSucceeded());
    }

    @Test
    void givenTransitionThrowsExceptionWhenExecuteThenTransitionResultMustSucceededEqualsFalse() {
        String event = "any";
        StatefulObject statefulObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(statefulObject, event)).thenReturn(transition);
        doThrow(RuntimeException.class).when(transition).transit(any(StateContext.class));

        TransitionResult transitionResult = lifecycleManager.execute(statefulObject, event);

        assertFalse(transitionResult.isSucceeded());
    }

    @Test
    void givenSuccessTransitionWhenExecuteThenTransitionResultMustContainSourceState() {
        String event = "any";
        StatefulObject expectedObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(expectedObject, event)).thenReturn(transition);
        when(transition.transit(any(StateContext.class))).thenReturn(true);
        when(transition.getSourceState()).thenReturn("NEW");

        TransitionResult transitionResult = lifecycleManager.execute(expectedObject, event);

        assertNotNull(transitionResult.getSourceState());
    }

    @Test
    void givenFailedTransitionWhenExecuteThenTransitionResultMustContainSourceState() {
        String event = "any";
        StatefulObject expectedObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(expectedObject, event)).thenReturn(transition);
        when(transition.transit(any(StateContext.class))).thenReturn(false);
        when(transition.getSourceState()).thenReturn("NEW");

        TransitionResult transitionResult = lifecycleManager.execute(expectedObject, event);

        assertNotNull(transitionResult.getSourceState());
    }

    @Test
    void givenTransitionThrowsExceptionWhenExecuteThenTransitionResultMustContainSourceState() {
        String event = "any";
        StatefulObject statefulObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(statefulObject, event)).thenReturn(transition);
        doThrow(RuntimeException.class).when(transition).transit(any(StateContext.class));
        when(transition.getSourceState()).thenReturn("NEW");

        TransitionResult transitionResult = lifecycleManager.execute(statefulObject, event);

        assertNotNull(transitionResult.getSourceState());
    }

    @Test
    void givenSuccessTransitionWhenExecuteThenSourceStateInTransitionResultEqualToSourceStateInTransition() {
        String event = "any";
        StatefulObject expectedObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(expectedObject, event)).thenReturn(transition);
        when(transition.transit(any(StateContext.class))).thenReturn(true);
        String expectedSourceState = "NEW";
        when(transition.getSourceState()).thenReturn(expectedSourceState);

        TransitionResult transitionResult = lifecycleManager.execute(expectedObject, event);

        assertEquals(expectedSourceState, transitionResult.getSourceState());
    }

    @Test
    void givenFailedTransitionWhenExecuteThenSourceStateInTransitionResultEqualToSourceStateInTransition() {
        String event = "any";
        StatefulObject expectedObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(expectedObject, event)).thenReturn(transition);
        when(transition.transit(any(StateContext.class))).thenReturn(false);
        String expectedSourceState = "NEW";
        when(transition.getSourceState()).thenReturn(expectedSourceState);

        TransitionResult transitionResult = lifecycleManager.execute(expectedObject, event);

        assertEquals(expectedSourceState, transitionResult.getSourceState());
    }

    @Test
    void givenTransitionThrowsExceptionWhenExecuteThenSourceStateInTransitionResultEqualToSourceStateInTransition() {
        String event = "any";
        StatefulObject expectedObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(expectedObject, event)).thenReturn(transition);
        doThrow(RuntimeException.class).when(transition).transit(any(StateContext.class));
        String expectedSourceState = "NEW";
        when(transition.getSourceState()).thenReturn(expectedSourceState);

        TransitionResult transitionResult = lifecycleManager.execute(expectedObject, event);

        assertEquals(expectedSourceState, transitionResult.getSourceState());
    }

    @Test
    void givenSuccessTransitionWhenExecuteThenTransitionResultMustContainTargetState() {
        String event = "any";
        StatefulObject expectedObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(expectedObject, event)).thenReturn(transition);
        when(transition.transit(any(StateContext.class))).thenReturn(true);
        when(transition.getTargetState()).thenReturn("COMPLETED");

        TransitionResult transitionResult = lifecycleManager.execute(expectedObject, event);

        assertNotNull(transitionResult.getTargetState());
    }

    @Test
    void givenFailedTransitionWhenExecuteThenTransitionResultMustContainTargetState() {
        String event = "any";
        StatefulObject expectedObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(expectedObject, event)).thenReturn(transition);
        when(transition.transit(any(StateContext.class))).thenReturn(false);
        when(transition.getTargetState()).thenReturn("COMPLETED");

        TransitionResult transitionResult = lifecycleManager.execute(expectedObject, event);

        assertNotNull(transitionResult.getTargetState());
    }

    @Test
    void givenTransitionThrowsExceptionWhenExecuteThenTransitionResultMustContainTargetState() {
        String event = "any";
        StatefulObject expectedObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(expectedObject, event)).thenReturn(transition);
        doThrow(RuntimeException.class).when(transition).transit(any(StateContext.class));
        when(transition.getTargetState()).thenReturn("COMPLETED");

        TransitionResult transitionResult = lifecycleManager.execute(expectedObject, event);

        assertNotNull(transitionResult.getTargetState());
    }

    @Test
    void givenSuccessTransitionWhenExecuteThenTargetStateInTransitionResultEqualToTargetStateInTransition() {
        String event = "any";
        StatefulObject expectedObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(expectedObject, event)).thenReturn(transition);
        when(transition.transit(any(StateContext.class))).thenReturn(true);
        String expectedTargetState = "COMPLETED";
        when(transition.getTargetState()).thenReturn(expectedTargetState);

        TransitionResult transitionResult = lifecycleManager.execute(expectedObject, event);

        assertEquals(expectedTargetState, transitionResult.getTargetState());
    }

    @Test
    void givenFailedTransitionWhenExecuteThenTargetStateInTransitionResultEqualToTargetStateInTransition() {
        String event = "any";
        StatefulObject expectedObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(expectedObject, event)).thenReturn(transition);
        when(transition.transit(any(StateContext.class))).thenReturn(false);
        String expectedTargetState = "COMPLETED";
        when(transition.getTargetState()).thenReturn(expectedTargetState);

        TransitionResult transitionResult = lifecycleManager.execute(expectedObject, event);

        assertEquals(expectedTargetState, transitionResult.getTargetState());
    }

    @Test
    void givenTransitionThrowsExceptionWhenExecuteThenTargetStateInTransitionResultEqualToTargetStateInTransition() {
        String event = "any";
        StatefulObject expectedObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(expectedObject, event)).thenReturn(transition);
        doThrow(RuntimeException.class).when(transition).transit(any(StateContext.class));
        String expectedTargetState = "COMPLETED";
        when(transition.getTargetState()).thenReturn(expectedTargetState);

        TransitionResult transitionResult = lifecycleManager.execute(expectedObject, event);

        assertEquals(expectedTargetState, transitionResult.getTargetState());
    }

    @Test
    void givenSuccessTransitionWhenExecuteThenTransitionResultMustContainStateContext() {
        String event = "any";
        StatefulObject expectedObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(expectedObject, event)).thenReturn(transition);
        when(transition.transit(any(StateContext.class))).thenReturn(true);

        TransitionResult transitionResult = lifecycleManager.execute(expectedObject, event);

        assertNotNull(transitionResult.getStateContext());
    }

    @Test
    void givenFailedTransitionWhenExecuteThenTransitionResultMustContainStateContext() {
        String event = "any";
        StatefulObject expectedObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(expectedObject, event)).thenReturn(transition);
        when(transition.transit(any(StateContext.class))).thenReturn(false);

        TransitionResult transitionResult = lifecycleManager.execute(expectedObject, event);

        assertNotNull(transitionResult.getStateContext());
    }

    @Test
    void givenTransitionThrowsExceptionWhenExecuteThenTransitionResultMustContainStateContext() {
        String event = "any";
        StatefulObject expectedObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(expectedObject, event)).thenReturn(transition);
        doThrow(RuntimeException.class).when(transition).transit(any(StateContext.class));

        TransitionResult transitionResult = lifecycleManager.execute(expectedObject, event);

        assertNotNull(transitionResult.getStateContext());
    }

    @Test
    void givenSuccessTransitionWhenExecuteThenStateContextInTransitionResultEqualToStateContextPassedToTransition() {
        String event = "any";
        StatefulObject expectedObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(expectedObject, event)).thenReturn(transition);
        ArgumentCaptor<StateContext> captor = ArgumentCaptor.forClass(StateContext.class);
        when(transition.transit(captor.capture())).thenReturn(true);

        TransitionResult transitionResult = lifecycleManager.execute(expectedObject, event);

        assertEquals(captor.getValue(), transitionResult.getStateContext());
    }

    @Test
    void givenFailedTransitionWhenExecuteThenStateContextInTransitionResultEqualToStateContextPassedToTransition() {
        String event = "any";
        StatefulObject expectedObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(expectedObject, event)).thenReturn(transition);
        ArgumentCaptor<StateContext> captor = ArgumentCaptor.forClass(StateContext.class);
        when(transition.transit(captor.capture())).thenReturn(false);

        TransitionResult transitionResult = lifecycleManager.execute(expectedObject, event);

        assertEquals(captor.getValue(), transitionResult.getStateContext());
    }

    @Test
    void givenTransitionThrowsExceptionWhenExecuteThenStateContextInTransitionResultEqualToStateContextPassedToTransition() {
        String event = "any";
        StatefulObject expectedObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(expectedObject, event)).thenReturn(transition);
        ArgumentCaptor<StateContext> captor = ArgumentCaptor.forClass(StateContext.class);
        doThrow(RuntimeException.class).when(transition).transit(captor.capture());

        TransitionResult transitionResult = lifecycleManager.execute(expectedObject, event);

        assertEquals(captor.getValue(), transitionResult.getStateContext());
    }

    @Test
    void givenTransitionThrowsExceptionWhenExecuteThenTransitionResultMustContainException() {
        String event = "any";
        StatefulObject expectedObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(expectedObject, event)).thenReturn(transition);
        doThrow(RuntimeException.class).when(transition).transit(any(StateContext.class));

        TransitionResult transitionResult = lifecycleManager.execute(expectedObject, event);

        assertNotNull(transitionResult.getException());
    }

    @Test
    void givenSuccessTransitionWhenExecuteThenStatefulObject_setTransitionMustBeCalled() {
        String event = "any";
        StatefulObject statefulObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(statefulObject, event)).thenReturn(transition);
        when(transition.transit(any(StateContext.class))).thenReturn(true);
        when(transition.getTargetState()).thenReturn("COMPLETED");

        lifecycleManager.execute(statefulObject, event);

        verify(statefulObject, times(1)).setState("COMPLETED");
    }

    @Test
    void givenFailedTransitionWhenExecuteThenStatefulObject_setTransitionMustNotBeCalled() {
        String event = "any";
        StatefulObject statefulObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(statefulObject, event)).thenReturn(transition);
        when(transition.transit(any(StateContext.class))).thenReturn(false);
        when(transition.getTargetState()).thenReturn("COMPLETED");

        lifecycleManager.execute(statefulObject, event);

        verify(statefulObject, times(0)).setState(anyString());
    }

    @Test
    void givenTransitionThrowsExceptionWhenExecuteThenStatefulObject_setTransitionMustNotBeCalled() {
        String event = "any";
        StatefulObject statefulObject = mock(StatefulObject.class);
        Transition transition = mock(Transition.class);
        when(transitionProvider.getTransition(statefulObject, event)).thenReturn(transition);
        doThrow(RuntimeException.class).when(transition).transit(any(StateContext.class));
        when(transition.getTargetState()).thenReturn("COMPLETED");

        lifecycleManager.execute(statefulObject, event);

        verify(statefulObject, times(0)).setState(anyString());
    }

    @Test
    void givenTransitionProviderThrowsExceptionWhenExecuteThenThrowsExceptionFromTransitionProvider() {
        String event = "any";
        StatefulObject statefulObject = mock(StatefulObject.class);
        doThrow(AmbiguousTransitionException.class).when(transitionProvider).getTransition(statefulObject, event);

        assertThrows(AmbiguousTransitionException.class, () -> lifecycleManager.execute(statefulObject, event));
    }
}