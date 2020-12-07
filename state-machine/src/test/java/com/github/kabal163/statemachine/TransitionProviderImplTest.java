package com.github.kabal163.statemachine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.github.kabal163.statemachine.api.StatefulObject;
import com.github.kabal163.statemachine.exception.AmbiguousTransitionException;
import com.github.kabal163.statemachine.exception.LifecycleNotFoundException;
import com.github.kabal163.statemachine.exception.TransitionNotFoundException;

import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static com.github.kabal163.statemachine.TestEvent.ANOTHER_EVENT;
import static com.github.kabal163.statemachine.TestEvent.EVENT;
import static com.github.kabal163.statemachine.TestState.ANOTHER_STATE;
import static com.github.kabal163.statemachine.TestState.STATE;

class TransitionProviderImplTest {

    static final String LIFECYCLE_NAME_1 = "lifecycle_1";
    static final String LIFECYCLE_NAME_2 = "lifecycle_2";

    @Mock
    Transition<TestState, TestEvent> transition1;

    @Mock
    Transition<TestState, TestEvent> transition2;

    @Mock
    Transition<TestState, TestEvent> transition3;

    TransitionProviderImpl<TestState, TestEvent> transitionProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        transitionProvider = new TransitionProviderImpl<>(getLifecycles());
    }

    @Test
    @DisplayName("Given StatefulObject is null " +
            "When call TransitionProviderImpl.getTransition " +
            "Then throws NullPointerException")
    void givenStatefulObjectIsNull_whenCallGetTransition_thenThrowsNullPointerException() {
        assertThatThrownBy(() -> transitionProvider.getTransition(null, EVENT))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("must not be null");
    }

    @Test
    @DisplayName("Given event is null " +
            "When call TransitionProviderImpl.getTransition " +
            "Then throws NullPointerException")
    void givenEventIsNull_whenCallGetTransition_thenThrowsNullPointerException() {
        assertThatThrownBy(() -> transitionProvider.getTransition(mock(StatefulObject.class), null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("must not be null");
    }

    @Test
    @DisplayName("Given StatefulObject returns non existent lifecycle name " +
            "When call TransitionProviderImpl.getTransition " +
            "Then throws LifecycleNotFoundException")
    void givenStatefulObjectReturnsNonExistentLifecycleName_whenGetTransition_thenThrowsLifecycleNotFoundException() {
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(statefulObject.getLifecycleName()).thenReturn("nonExistent");

        assertThatThrownBy(() -> transitionProvider.getTransition(statefulObject, EVENT))
                .isInstanceOf(LifecycleNotFoundException.class)
                .hasMessageContaining("There is no such lifecycle");
    }

    @Test
    @DisplayName("Given StatefulObject.getLifecycleName returns null " +
            "When call TransitionProviderImpl.getTransition " +
            "Then throws LifecycleNotFoundException")
    void givenStatefulObject$getLifecycleNameReturnsNull_whenGetTransition_thenThrowsLifecycleNotFoundException() {
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(statefulObject.getLifecycleName()).thenReturn(null);

        assertThatThrownBy(() -> transitionProvider.getTransition(statefulObject, EVENT))
                .isInstanceOf(LifecycleNotFoundException.class)
                .hasMessageContaining("Null or empty lifecycle names are not supported");
    }

    @Test
    @DisplayName("Given StatefulObject.getLifecycleName returns empty string " +
            "When call TransitionProviderImpl.getTransition " +
            "Then throws LifecycleNotFoundException")
    void givenStatefulObject$getLifecycleNameReturnsEmptyString_whenGetTransition_thenThrowsLifecycleNotFoundException() {
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(statefulObject.getLifecycleName()).thenReturn(EMPTY);

        assertThatThrownBy(() -> transitionProvider.getTransition(statefulObject, EVENT))
                .isInstanceOf(LifecycleNotFoundException.class)
                .hasMessageContaining("Null or empty lifecycle names are not supported");
    }

    @Test
    @DisplayName("When call TransitionProviderImpl.getTransition " +
            "Then returns not null")
    void whenCallGetTransition_thenReturnsNotNull() {
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(transition1.getSourceState()).thenReturn(STATE);
        when(transition1.getEvent()).thenReturn(EVENT);
        when(statefulObject.getLifecycleName()).thenReturn(LIFECYCLE_NAME_1);
        when(statefulObject.getState()).thenReturn(STATE);

        Transition<TestState, TestEvent> actual = transitionProvider.getTransition(statefulObject, EVENT);

        assertThat(actual).isNotNull();
    }

    @Test
    @DisplayName("Given StatefulObject.getState returns not matching source state " +
            "When call TransitionProviderImpl.getTransition " +
            "Then throws TransitionNotFoundException")
    void givenNotMatchingSourceState_whenGetTransition_thenThrowsTransitionNotFoundException() {
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(transition3.getSourceState()).thenReturn(STATE);
        when(transition3.getEvent()).thenReturn(EVENT);
        when(statefulObject.getLifecycleName()).thenReturn(LIFECYCLE_NAME_2);
        when(statefulObject.getState()).thenReturn(ANOTHER_STATE);

        assertThatThrownBy(() -> transitionProvider.getTransition(statefulObject, EVENT))
                .isInstanceOf(TransitionNotFoundException.class)
                .hasMessageContaining("no matching transition");
    }

    @Test
    @DisplayName("Given StatefulObject.getState returns not matching event " +
            "When call TransitionProviderImpl.getTransition " +
            "Then throws TransitionNotFoundException")
    void givenNotMatchingEvent_whenGetTransition_thenThrowsTransitionNotFoundException() {
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(transition3.getSourceState()).thenReturn(STATE);
        when(transition3.getEvent()).thenReturn(EVENT);
        when(statefulObject.getLifecycleName()).thenReturn(LIFECYCLE_NAME_2);
        when(statefulObject.getState()).thenReturn(STATE);

        assertThatThrownBy(() -> transitionProvider.getTransition(statefulObject, ANOTHER_EVENT))
                .isInstanceOf(TransitionNotFoundException.class)
                .hasMessageContaining("no matching transition");
    }

    @Test
    @DisplayName("Given two transitions with same source state and event " +
            "When call TransitionProviderImpl.getTransition " +
            "Then throws AmbiguousTransitionException")
    void givenTwoTransitionsWithSameSourceStateAndEvent_whenGetTransition_thenThrowsAmbiguousTransitionException() {
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(transition1.getSourceState()).thenReturn(STATE);
        when(transition1.getEvent()).thenReturn(EVENT);
        when(transition2.getSourceState()).thenReturn(STATE);
        when(transition2.getEvent()).thenReturn(EVENT);
        when(statefulObject.getLifecycleName()).thenReturn(LIFECYCLE_NAME_1);
        when(statefulObject.getState()).thenReturn(STATE);

        assertThatThrownBy(() -> transitionProvider.getTransition(statefulObject, EVENT))
                .isInstanceOf(AmbiguousTransitionException.class)
                .hasMessageContaining("more then one transition");
    }

    @Test
    @DisplayName("When call TransitionProviderImpl.getTransition " +
            "Then StatefulObject.getState must be call at least once")
    void whenGetTransition_thenStatefulObject$getStateMustBeCalledAtLeastOnce() {
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(transition1.getSourceState()).thenReturn(STATE);
        when(transition1.getEvent()).thenReturn(EVENT);
        when(statefulObject.getState()).thenReturn(STATE);
        when(statefulObject.getLifecycleName()).thenReturn(LIFECYCLE_NAME_1);

        transitionProvider.getTransition(statefulObject, EVENT);

        verify(statefulObject, atLeastOnce()).getState();
    }

    @Test
    @DisplayName("When call TransitionProviderImpl.getTransition " +
            "Then StatefulObject.getLifecycleName must be call at least once")
    void whenGetTransition_thenStatefulObject$getLifecycleNameMustBeCalledAtLeastOnce() {
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(transition1.getSourceState()).thenReturn(STATE);
        when(transition1.getEvent()).thenReturn(EVENT);
        when(statefulObject.getState()).thenReturn(STATE);
        when(statefulObject.getLifecycleName()).thenReturn(LIFECYCLE_NAME_1);

        transitionProvider.getTransition(statefulObject, EVENT);

        verify(statefulObject, atLeastOnce()).getLifecycleName();
    }

    private Map<String, Lifecycle<TestState, TestEvent>> getLifecycles() {
        Lifecycle<TestState, TestEvent> lifecycle1 = LifecycleImpl.<TestState, TestEvent>builder()
                .name(LIFECYCLE_NAME_1)
                .transitions(Set.of(transition1, transition2))
                .build();
        Lifecycle<TestState, TestEvent> lifecycle2 = LifecycleImpl.<TestState, TestEvent>builder()
                .name(LIFECYCLE_NAME_2)
                .transitions(Set.of(transition3))
                .build();
        return Map.of(
                LIFECYCLE_NAME_1, lifecycle1,
                LIFECYCLE_NAME_2, lifecycle2);
    }
}