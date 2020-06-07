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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

class LifecycleManagerImplTest {

    LifecycleManagerImpl<State, Event> lifecycleManager;

    LifecycleConfiguration<State, Event> lifecycleConfiguration;
    TransitionBuilder<State, Event> transitionBuilder;
    Transition<State, Event> transitionA;
    Transition<State, Event> transitionB;

    @SuppressWarnings("unchecked")
    LifecycleManagerImplTest() {
        lifecycleConfiguration = Mockito.mock(LifecycleConfiguration.class);
        transitionBuilder = Mockito.mock(TransitionBuilder.class);
        transitionA = Mockito.mock(Transition.class);
        transitionB = Mockito.mock(Transition.class);
    }

    @BeforeEach
    void setUp() {
        Mockito.when(transitionA.getSourceState()).thenReturn(NEW);
        Mockito.when(transitionA.getTargetState()).thenReturn(APPROVED);
        Mockito.when(transitionA.getEvent()).thenReturn(APPROVE);
        Mockito.when(transitionA.transit(any())).thenReturn(true);

        Mockito.when(transitionB.getSourceState()).thenReturn(NEW);
        Mockito.when(transitionB.getTargetState()).thenReturn(CANCELED);
        Mockito.when(transitionB.getEvent()).thenReturn(CANCEL);
        Mockito.when(transitionB.transit(any())).thenReturn(true);

        Mockito.when(transitionBuilder.buildTransitions()).thenReturn(Set.of(transitionA, transitionB));

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

        Mockito.verify(transitionA, Mockito.times(1)).transit(any());
        Mockito.verify(transitionB, Mockito.times(0)).transit(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void transitionBMustBeUsed() {
        StatefulObject<State> statefulObject = Mockito.mock(StatefulObject.class);
        Mockito.when(statefulObject.getState()).thenReturn(NEW);
        Mockito.doNothing().when(statefulObject).setState(any());

        lifecycleManager.execute(statefulObject, CANCEL);

        Mockito.verify(transitionA, Mockito.times(0)).transit(any());
        Mockito.verify(transitionB, Mockito.times(1)).transit(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void contextMustContainStatefulObject() {
        StatefulObject<State> statefulObject = Mockito.mock(StatefulObject.class);
        Mockito.when(statefulObject.getState()).thenReturn(NEW);
        Mockito.doNothing().when(statefulObject).setState(any());

        ArgumentCaptor<StateContext<State, Event>> contextCaptor = ArgumentCaptor.forClass(StateContext.class);
        Mockito.when(transitionA.transit(contextCaptor.capture())).thenReturn(true);

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
        Mockito.when(transitionA.transit(contextCaptor.capture())).thenReturn(true);

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
        Mockito.when(transitionA.transit(contextCaptor.capture())).thenReturn(true);
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
        Mockito.when(transitionA.transit(contextCaptor.capture())).thenReturn(true);

        lifecycleManager.execute(statefulObject, APPROVE);
        StateContext<State, Event> context = contextCaptor.getValue();
        context.putVariable("testKey", "testValue");

        assertEquals("testValue", context.getVariable("testKey", String.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void exceptionDueToAmbiguousTransition() {
        Mockito.when(transitionB.getEvent()).thenReturn(APPROVE);

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