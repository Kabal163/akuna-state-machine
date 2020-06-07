package com.github.kabal163.statemachine;

import com.github.kabal163.statemachine.api.LifecycleConfiguration;
import com.github.kabal163.statemachine.api.StateContext;
import com.github.kabal163.statemachine.api.StatefulObject;
import com.github.kabal163.statemachine.api.TransitionResult;
import com.github.kabal163.statemachine.exception.AmbiguousTransitionException;
import com.github.kabal163.statemachine.exception.TransitionNotFoundException;
import com.github.kabal163.statemachine.testimpl.Event;
import com.github.kabal163.statemachine.testimpl.State;
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

import static com.github.kabal163.statemachine.testimpl.Event.APPROVE;
import static com.github.kabal163.statemachine.testimpl.Event.CANCEL;
import static com.github.kabal163.statemachine.testimpl.State.APPROVED;
import static com.github.kabal163.statemachine.testimpl.State.CANCELED;
import static com.github.kabal163.statemachine.testimpl.State.NEW;
import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

class LifecycleManagerImplTest {

    LifecycleManagerImpl<State, Event> lifecycleManager;

    LifecycleConfiguration<State, Event> lifecycleConfiguration;
    TransitionBuilder<State, Event> transitionBuilder;
    Transition<State, Event> approveTransition;
    Transition<State, Event> cancelTransition;

    @SuppressWarnings("unchecked")
    LifecycleManagerImplTest() {
        lifecycleConfiguration = Mockito.mock(LifecycleConfiguration.class);
        transitionBuilder = Mockito.mock(TransitionBuilder.class);
        approveTransition = Mockito.mock(Transition.class);
        cancelTransition = Mockito.mock(Transition.class);
    }

    @BeforeEach
    void setUp() {
        Mockito.when(approveTransition.getSourceState()).thenReturn(NEW);
        Mockito.when(approveTransition.getTargetState()).thenReturn(APPROVED);
        Mockito.when(approveTransition.getEvent()).thenReturn(APPROVE);
        Mockito.when(approveTransition.transit(any())).thenReturn(true);

        Mockito.when(cancelTransition.getSourceState()).thenReturn(NEW);
        Mockito.when(cancelTransition.getTargetState()).thenReturn(CANCELED);
        Mockito.when(cancelTransition.getEvent()).thenReturn(CANCEL);
        Mockito.when(cancelTransition.transit(any())).thenReturn(true);

        Mockito.when(transitionBuilder.buildTransitions()).thenReturn(Set.of(approveTransition, cancelTransition));

        lifecycleManager = new LifecycleManagerImpl<>(transitionBuilder, lifecycleConfiguration);
        lifecycleManager.init();
    }

    @ParameterizedTest
    @MethodSource("getValidStatefulObjects")
    void transitionMustBeFound(StatefulObject<State> statefulObject, Event event) {
        TransitionResult<State, Event> result = lifecycleManager.execute(statefulObject, event);
        assertTrue(result.isSuccess());
    }

    @Test
    @SuppressWarnings("unchecked")
    void transitionAMustBeUsed() {
        StatefulObject<State> statefulObject = Mockito.mock(StatefulObject.class);
        Mockito.when(statefulObject.getState()).thenReturn(NEW);
        Mockito.doNothing().when(statefulObject).setState(any());

        lifecycleManager.execute(statefulObject, APPROVE);

        Mockito.verify(approveTransition, Mockito.times(1)).transit(any());
        Mockito.verify(cancelTransition, Mockito.times(0)).transit(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void transitionBMustBeUsed() {
        StatefulObject<State> statefulObject = Mockito.mock(StatefulObject.class);
        Mockito.when(statefulObject.getState()).thenReturn(NEW);
        Mockito.doNothing().when(statefulObject).setState(any());

        lifecycleManager.execute(statefulObject, CANCEL);

        Mockito.verify(approveTransition, Mockito.times(0)).transit(any());
        Mockito.verify(cancelTransition, Mockito.times(1)).transit(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void contextMustContainStatefulObject() {
        StatefulObject<State> statefulObject = Mockito.mock(StatefulObject.class);
        Mockito.when(statefulObject.getState()).thenReturn(NEW);
        Mockito.doNothing().when(statefulObject).setState(any());

        ArgumentCaptor<StateContext<State, Event>> contextCaptor = ArgumentCaptor.forClass(StateContext.class);
        Mockito.when(approveTransition.transit(contextCaptor.capture())).thenReturn(true);

        lifecycleManager.execute(statefulObject, APPROVE);
        StateContext<State, Event> context = contextCaptor.getValue();

        assertEquals(statefulObject, context.getStatefulObject());
    }

    @Test
    @SuppressWarnings("unchecked")
    void contextMustContainEvent() {
        StatefulObject<State> statefulObject = Mockito.mock(StatefulObject.class);
        Mockito.when(statefulObject.getState()).thenReturn(NEW);
        Mockito.doNothing().when(statefulObject).setState(any());

        ArgumentCaptor<StateContext<State, Event>> contextCaptor = ArgumentCaptor.forClass(StateContext.class);
        Mockito.when(approveTransition.transit(contextCaptor.capture())).thenReturn(true);

        lifecycleManager.execute(statefulObject, APPROVE);
        StateContext<State, Event> context = contextCaptor.getValue();

        assertEquals(APPROVE, context.getEvent());
    }

    @Test
    @SuppressWarnings("unchecked")
    void contextMustContainVariables() {
        StatefulObject<State> statefulObject = Mockito.mock(StatefulObject.class);
        Mockito.when(statefulObject.getState()).thenReturn(NEW);
        Mockito.doNothing().when(statefulObject).setState(any());

        ArgumentCaptor<StateContext<State, Event>> contextCaptor = ArgumentCaptor.forClass(StateContext.class);
        Mockito.when(approveTransition.transit(contextCaptor.capture())).thenReturn(true);
        Map<String, Object> variables = singletonMap("testKey", "testValue");

        lifecycleManager.execute(statefulObject, APPROVE, variables);
        StateContext<State, Event> context = contextCaptor.getValue();

        assertEquals("testValue", context.getVariable("testKey", String.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void contextVariablesMustBeAppendable() {
        StatefulObject<State> statefulObject = Mockito.mock(StatefulObject.class);
        Mockito.when(statefulObject.getState()).thenReturn(NEW);
        Mockito.doNothing().when(statefulObject).setState(any());

        ArgumentCaptor<StateContext<State, Event>> contextCaptor = ArgumentCaptor.forClass(StateContext.class);
        Mockito.when(approveTransition.transit(contextCaptor.capture())).thenReturn(true);

        lifecycleManager.execute(statefulObject, APPROVE);
        StateContext<State, Event> context = contextCaptor.getValue();
        context.putVariable("testKey", "testValue");

        assertEquals("testValue", context.getVariable("testKey", String.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void exceptionDueToAmbiguousTransition() {
        Mockito.when(cancelTransition.getEvent()).thenReturn(APPROVE);

        StatefulObject<State> statefulObject = Mockito.mock(StatefulObject.class);
        Mockito.when(statefulObject.getState()).thenReturn(NEW);
        Mockito.doNothing().when(statefulObject).setState(any());

        assertThrows(AmbiguousTransitionException.class, () -> lifecycleManager.execute(statefulObject, APPROVE));
    }

    @Test
    @SuppressWarnings("unchecked")
    void exceptionDueToNoSuchTransition() {
        StatefulObject<State> statefulObject = Mockito.mock(StatefulObject.class);
        Mockito.when(statefulObject.getState()).thenReturn(APPROVED);
        Mockito.doNothing().when(statefulObject).setState(any());

        assertThrows(TransitionNotFoundException.class, () -> lifecycleManager.execute(statefulObject, APPROVE));
    }

    @Test
    @SuppressWarnings("unchecked")
    void transitionResultContainsExceptionIfItHappened() {
        Mockito.when(approveTransition.transit(any())).thenThrow(RuntimeException.class);

        StatefulObject<State> statefulObject = Mockito.mock(StatefulObject.class);
        Mockito.when(statefulObject.getState()).thenReturn(NEW);
        Mockito.doNothing().when(statefulObject).setState(any());

        TransitionResult<State, Event> result = lifecycleManager.execute(statefulObject, APPROVE);

        assertNotNull(result.getException());
        assertEquals(RuntimeException.class, result.getException().getClass());
    }

    @Test
    @SuppressWarnings("unchecked")
    void transitionResultSuccessIsFalseIfTransitionReturnsFalse() {
        Mockito.when(approveTransition.transit(any())).thenReturn(false);

        StatefulObject<State> statefulObject = Mockito.mock(StatefulObject.class);
        Mockito.when(statefulObject.getState()).thenReturn(NEW);
        Mockito.doNothing().when(statefulObject).setState(any());

        TransitionResult<State, Event> result = lifecycleManager.execute(statefulObject, APPROVE);

        assertFalse(result.isSuccess());
    }

    @Test
    @SuppressWarnings("unchecked")
    void transitionResultSuccessIsFalseIfTransitionThrowsException() {
        Mockito.when(approveTransition.transit(any())).thenThrow(RuntimeException.class);

        StatefulObject<State> statefulObject = Mockito.mock(StatefulObject.class);
        Mockito.when(statefulObject.getState()).thenReturn(NEW);
        Mockito.doNothing().when(statefulObject).setState(any());

        TransitionResult<State, Event> result = lifecycleManager.execute(statefulObject, APPROVE);

        assertFalse(result.isSuccess());
    }

    @Test
    @SuppressWarnings("unchecked")
    void stateMustBeChangedOnTargetIfTransitionIsSuccessful() {
        ArgumentCaptor<State> stateCaptor = ArgumentCaptor.forClass(State.class);
        StatefulObject<State> statefulObject = Mockito.mock(StatefulObject.class);
        Mockito.when(statefulObject.getState()).thenReturn(NEW);
        Mockito.doNothing().when(statefulObject).setState(stateCaptor.capture());

        lifecycleManager.execute(statefulObject, APPROVE);

        State actualTargetState = stateCaptor.getValue();
        State expectedTargetState = approveTransition.getTargetState();

        assertEquals(expectedTargetState, actualTargetState);
    }

    @Test
    @SuppressWarnings("unchecked")
    void stateMustNotBeChangedIfTransitionReturnsFalse() {
        Mockito.when(approveTransition.transit(any())).thenReturn(false);

        StatefulObject<State> statefulObject = Mockito.mock(StatefulObject.class);
        Mockito.when(statefulObject.getState()).thenReturn(NEW);
        Mockito.doNothing().when(statefulObject).setState(any());

        lifecycleManager.execute(statefulObject, APPROVE);

        Mockito.verify(statefulObject, Mockito.times(0)).setState(approveTransition.getTargetState());
    }

    @Test
    @SuppressWarnings("unchecked")
    void stateMustNotBeChangedIfTransitionThrowsException() {
        Mockito.when(approveTransition.transit(any())).thenThrow(RuntimeException.class);

        StatefulObject<State> statefulObject = Mockito.mock(StatefulObject.class);
        Mockito.when(statefulObject.getState()).thenReturn(NEW);
        Mockito.doNothing().when(statefulObject).setState(any());

        lifecycleManager.execute(statefulObject, APPROVE);

        Mockito.verify(statefulObject, Mockito.times(0)).setState(approveTransition.getTargetState());
    }

    @SuppressWarnings("unchecked")
    public static Stream<Arguments> getValidStatefulObjects() {
        StatefulObject<State> o1 = Mockito.mock(StatefulObject.class);
        StatefulObject<State> o2 = Mockito.mock(StatefulObject.class);

        Mockito.when(o1.getState()).thenReturn(NEW);
        Mockito.doNothing().when(o1).setState(any());
        Mockito.when(o2.getState()).thenReturn(NEW);
        Mockito.doNothing().when(o2).setState(any());

        return Stream.of(
                Arguments.of(o1, APPROVE),
                Arguments.of(o1, CANCEL)
        );
    }
}