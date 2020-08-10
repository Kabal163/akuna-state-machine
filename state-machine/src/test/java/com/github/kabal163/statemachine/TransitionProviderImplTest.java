package com.github.kabal163.statemachine;

import com.github.kabal163.statemachine.api.LifecycleConfiguration;
import com.github.kabal163.statemachine.api.StatefulObject;
import com.github.kabal163.statemachine.api.TransitionsInitializer;
import com.github.kabal163.statemachine.exception.AmbiguousTransitionException;
import com.github.kabal163.statemachine.exception.LifecycleNotFoundException;
import com.github.kabal163.statemachine.exception.TransitionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.AtLeast;

import java.util.Map;
import java.util.Set;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransitionProviderImplTest {

    static final String LIFECYCLE_NAME_1 = "lifecycle_1";
    static final String LIFECYCLE_NAME_2 = "lifecycle_2";

    @Mock
    TransitionsInitializer transitionsInitializer;

    @Mock
    LifecycleConfiguration lifecycleConfiguration;

    @Mock
    Transition transition1;

    @Mock
    Transition transition2;

    @Mock
    Transition transition3;

    TransitionProviderImpl transitionProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(transitionsInitializer.initialize(any()))
                .thenReturn(
                        Map.of(
                                LIFECYCLE_NAME_1, Set.of(transition1, transition2),
                                LIFECYCLE_NAME_2, Set.of(transition3)));
        transitionProvider = TransitionProviderImpl.builder()
                .transitionInitializer(transitionsInitializer)
                .configs(singletonList(lifecycleConfiguration))
                .build();
    }

    @Test
    void givenStatefulObjectIsNullWhenGetTransitionThenThrowsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> transitionProvider.getTransition(null, "anyEvent"));
    }

    @Test
    void givenEventIsNullWhenGetTransitionThenThrowsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> transitionProvider.getTransition(mock(StatefulObject.class), null));
    }

    @Test
    void givenEventIsEmptyWhenGetTransitionThenThrowsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> transitionProvider.getTransition(mock(StatefulObject.class), ""));
    }

    @Test
    void givenStatefulObjectReturnsNotExistingLifecycleNameWhenGetTransitionThenThrowsLifecycleNotFoundException() {
        StatefulObject statefulObject = mock(StatefulObject.class);
        when(statefulObject.getLifecycleName()).thenReturn("NOT_EXISTING_LIFECYCLE_NAME");

        assertThrows(
                LifecycleNotFoundException.class,
                () -> transitionProvider.getTransition(statefulObject, "anyEvent"));
    }

    @Test
    void givenTransition1ContainsMatchingSourceStateAndEventWhenGetTransitionThenReturnTransition1() {
        StatefulObject statefulObject = mock(StatefulObject.class);
        when(statefulObject.getLifecycleName()).thenReturn(LIFECYCLE_NAME_1);
        when(statefulObject.getState()).thenReturn("testState");
        when(transition1.getSourceState()).thenReturn("testState");
        when(transition1.getEvent()).thenReturn("testEvent");

        Transition actualResult = transitionProvider.getTransition(statefulObject, "testEvent");

        assertEquals(transition1, actualResult);
    }

    @Test
    void givenThereIsNotMatchingTransitionsDueToWrongStateWhenGetTransitionThenThrowsTransitionNotFoundException() {
        StatefulObject statefulObject = mock(StatefulObject.class);
        when(statefulObject.getLifecycleName()).thenReturn(LIFECYCLE_NAME_2);
        when(statefulObject.getState()).thenReturn("wrongState");
        when(transition3.getSourceState()).thenReturn("testState");
        when(transition3.getEvent()).thenReturn("testEvent");

        assertThrows(
                TransitionNotFoundException.class,
                () -> transitionProvider.getTransition(statefulObject, "testEvent"));
    }

    @Test
    void givenThereIsNotMatchingTransitionsDueToWrongEventWhenGetTransitionThenThrowsTransitionNotFoundException() {
        StatefulObject statefulObject = mock(StatefulObject.class);
        when(statefulObject.getLifecycleName()).thenReturn(LIFECYCLE_NAME_2);
        when(statefulObject.getState()).thenReturn("testState");
        when(transition3.getSourceState()).thenReturn("testState");
        when(transition3.getEvent()).thenReturn("testEvent");

        assertThrows(
                TransitionNotFoundException.class,
                () -> transitionProvider.getTransition(statefulObject, "wrongEvent"));
    }

    @Test
    void givenExistsTwoTransitionsWithSameSourceStateAndEventWhenGetTransitionThenThrowsAmbiguousTransitionException() {
        StatefulObject statefulObject = mock(StatefulObject.class);
        when(statefulObject.getLifecycleName()).thenReturn(LIFECYCLE_NAME_1);
        when(statefulObject.getState()).thenReturn("sameState");
        when(transition1.getSourceState()).thenReturn("sameState");
        when(transition1.getEvent()).thenReturn("sameEvent");
        when(transition2.getSourceState()).thenReturn("sameState");
        when(transition2.getEvent()).thenReturn("sameEvent");

        assertThrows(
                AmbiguousTransitionException.class,
                () -> transitionProvider.getTransition(statefulObject, "sameEvent"));
    }

    @Test
    void givenStatefulObjectReturnsNullLifecycleNameWhenGetTransitionThenThrowsLifecycleNotFoundException() {
        StatefulObject statefulObject = mock(StatefulObject.class);
        when(statefulObject.getLifecycleName()).thenReturn(null);

        assertThrows(
                LifecycleNotFoundException.class,
                () -> transitionProvider.getTransition(statefulObject, "anyEvent"));
    }

    @Test
    void givenStatefulObjectReturnsEmptyLifecycleNameWhenGetTransitionThenThrowsLifecycleNotFoundException() {
        StatefulObject statefulObject = mock(StatefulObject.class);
        when(statefulObject.getLifecycleName()).thenReturn("");

        assertThrows(
                LifecycleNotFoundException.class,
                () -> transitionProvider.getTransition(statefulObject, "anyEvent"));
    }

    /**
     * Provider must match transition's source state with stateful
     * object's current state
     */
    @Test
    void whenGetTransitionThenStatefulObject_getStateMustBeCalled() {
        StatefulObject statefulObject = mock(StatefulObject.class);
        when(statefulObject.getState()).thenReturn("testState");
        when(statefulObject.getLifecycleName()).thenReturn(LIFECYCLE_NAME_1);
        when(transition1.getSourceState()).thenReturn("testState");
        when(transition1.getEvent()).thenReturn("testEvent");

        transitionProvider.getTransition(statefulObject, "testEvent");

        verify(statefulObject, times(1)).getState();
    }

    /**
     * Provider must search transitions according the lifecycle name
     * provided by the stateful object
     */
    @Test
    void whenGetTransitionThenStatefulObject_getLifecycleNameMustBeCalled() {
        StatefulObject statefulObject = mock(StatefulObject.class);
        when(statefulObject.getState()).thenReturn("testState");
        when(statefulObject.getLifecycleName()).thenReturn(LIFECYCLE_NAME_1);
        when(transition1.getSourceState()).thenReturn("testState");
        when(transition1.getEvent()).thenReturn("testEvent");

        transitionProvider.getTransition(statefulObject, "testEvent");

        verify(statefulObject, new AtLeast(1)).getLifecycleName();
    }
}