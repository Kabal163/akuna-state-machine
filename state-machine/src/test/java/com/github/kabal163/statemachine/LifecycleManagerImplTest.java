package com.github.kabal163.statemachine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.github.kabal163.statemachine.api.StateContext;
import com.github.kabal163.statemachine.api.StatefulObject;
import com.github.kabal163.statemachine.api.TransitionResult;
import com.github.kabal163.statemachine.exception.AmbiguousTransitionException;

import java.util.Map;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.filter;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static com.github.kabal163.statemachine.TestEvent.EVENT;
import static com.github.kabal163.statemachine.TestState.STATE;

class LifecycleManagerImplTest {

    @Mock
    TransitionProvider<TestState, TestEvent> transitionProviderMock;

    @Mock
    Transition<TestState, TestEvent> transitionMock;

    /**
     * Captures StateContext which is passed to the {@link Transition#transit(StateContext)}
     */
    ArgumentCaptor<StateContext<TestState, TestEvent>> transitionStateContextCaptor;

    LifecycleManagerImpl<TestState, TestEvent> lifecycleManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        transitionStateContextCaptor = ArgumentCaptor.forClass(StateContext.class);
        when(transitionMock.transit(transitionStateContextCaptor.capture())).thenReturn(true);

        lifecycleManager = new LifecycleManagerImpl<>(transitionProviderMock);
    }

    @Test
    @DisplayName("Given StatefulObject is null " +
            "When call LifecycleManagerImpl.execute(StatefulObject, E) " +
            "Then throws NullPointerException")
    void givenStatefulObjectIsNull_whenCallExecute_thenThrowsNullPointerException_1() {
        assertThatThrownBy(() -> lifecycleManager.execute(null, EVENT))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("must not be null");
    }

    @Test
    @DisplayName("Given StatefulObject is null " +
            "When call LifecycleManagerImpl.execute(StatefulObject, E, Map<String, Object> ) " +
            "Then throws NullPointerException")
    void givenStatefulObjectIsNull_whenCallExecute_thenThrowsNullPointerException_2() {
        Map<String, Object> emptyMap = emptyMap();
        assertThatThrownBy(() -> lifecycleManager.execute(null, EVENT, emptyMap))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("must not be null");
    }

    @Test
    @DisplayName("Given event is null " +
            "When call LifecycleManagerImpl.execute(StatefulObject, E) " +
            "Then throws NullPointerException")
    void givenEventIsNull_whenCallExecute_thenThrowsNullPointerException_1() {
        assertThatThrownBy(() -> lifecycleManager.execute(mock(StatefulObject.class), null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("must not be null");
    }

    @Test
    @DisplayName("Given event is null " +
            "When call LifecycleManagerImpl.execute(StatefulObject, E, Map<String, Object> ) " +
            "Then throws NullPointerException")
    void givenEventIsNull_whenCallExecute_thenThrowsNullPointerException_2() {
        Map<String, Object> emptyMap = emptyMap();
        assertThatThrownBy(() -> lifecycleManager.execute(mock(StatefulObject.class), null, emptyMap))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("must not be null");
    }

    @Test
    @DisplayName("Given variables map is null " +
            "When call LifecycleManagerImpl.execute(StatefulObject, E, Map<String, Object> ) " +
            "Then throws NullPointerException")
    void givenVariablesMapIsNull_whenCallExecute_thenThrowsNullPointerException() {
        assertThatThrownBy(() -> lifecycleManager.execute(mock(StatefulObject.class), EVENT, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("must not be null");
    }


    @Test
    @DisplayName("When call LifecycleManagerImpl.execute " +
            "Then TransitionProvider.getTransition must be called once in order to get available transition")
    void whenCallExecute_thenTransitionProvider$getTransitionMethodIsCalledOnce() {
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(transitionProviderMock.getTransition(statefulObject, EVENT)).thenReturn(transitionMock);

        lifecycleManager.execute(statefulObject, EVENT);

        verify(transitionProviderMock, times(1)).getTransition(statefulObject, EVENT);
    }

    @Test
    @DisplayName("When call LifecycleManagerImpl.execute " +
            "Then Transition.transit must be called once in order to execute available transition")
    void whenCallExecute_thenTransition$transitIsCalledOnce() {
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(transitionProviderMock.getTransition(statefulObject, EVENT)).thenReturn(transitionMock);

        lifecycleManager.execute(statefulObject, EVENT);

        verify(transitionMock, times(1)).transit(any(StateContext.class));
    }

    @Test
    @DisplayName("When call LifecycleManagerImpl.execute " +
            "Then StateContext must be passed to the Transition.transit method")
    void whenCallExecute_thenStateContextMustBePassedToTransition$transitMethod() {
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(transitionProviderMock.getTransition(statefulObject, EVENT)).thenReturn(transitionMock);

        lifecycleManager.execute(statefulObject, EVENT);
        StateContext<TestState, TestEvent> actual = transitionStateContextCaptor.getValue();

        assertThat(actual).isNotNull();
    }

    @Test
    @DisplayName("When call LifecycleManagerImpl.execute " +
            "Then StateContext must contain StatefulObject")
    void whenCallExecute_thenStateContextMustContainStatefulObject() {
        StatefulObject<TestState> expected = mock(StatefulObject.class);
        when(transitionProviderMock.getTransition(expected, EVENT)).thenReturn(transitionMock);

        lifecycleManager.execute(expected, EVENT);
        StatefulObject<TestState> actual = transitionStateContextCaptor.getValue().getStatefulObject();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("When call LifecycleManagerImpl.execute " +
            "Then StateContext must contain event")
    void whenCallExecute_thenStateContextMustContainEvent() {
        final TestEvent expected = EVENT;
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(transitionProviderMock.getTransition(statefulObject, expected)).thenReturn(transitionMock);

        lifecycleManager.execute(statefulObject, expected);
        TestEvent actual = transitionStateContextCaptor.getValue().getEvent();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Given custom variables" +
            "When call LifecycleManagerImpl.execute " +
            "Then StateContext must contain the custom variables")
    void givenCustomVariables_whenCallExecute_thenStateContextMustContainTheCustomVariables() {
        final String expected = "someValue";
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(transitionProviderMock.getTransition(statefulObject, EVENT)).thenReturn(transitionMock);
        Map<String, Object> variables = singletonMap("any", expected);

        lifecycleManager.execute(statefulObject, EVENT, variables);
        String actual = transitionStateContextCaptor.getValue().getVariable("any", String.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("When call LifecycleManagerImpl.execute " +
            "Then returns not null")
    void whenCallExecute_thenReturnsNotNull() {
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(transitionProviderMock.getTransition(statefulObject, EVENT)).thenReturn(transitionMock);

        TransitionResult<TestState, TestEvent> actual = lifecycleManager.execute(statefulObject, EVENT);

        assertThat(actual).isNotNull();
    }

    @Test
    @DisplayName("Given Transition.transit returns false " +
            "When call LifecycleManagerImpl.execute " +
            "Then returns not null")
    void givenTransition$transitReturnsFalse_whenCallExecute_thenReturnsNotNull() {
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(transitionMock.transit(any(StateContext.class))).thenReturn(false);
        when(transitionProviderMock.getTransition(statefulObject, EVENT)).thenReturn(transitionMock);

        TransitionResult<TestState, TestEvent> actual = lifecycleManager.execute(statefulObject, EVENT);

        assertThat(actual).isNotNull();
    }

    @Test
    @DisplayName("GivenTransition.transit throws an exception " +
            "When call LifecycleManagerImpl.execute " +
            "Then returns not null")
    void givenTransition$transitThrowsException_whenCallExecute_thenReturnNotNull() {
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        doThrow(RuntimeException.class).when(transitionMock).transit(any(StateContext.class));
        when(transitionProviderMock.getTransition(statefulObject, EVENT)).thenReturn(transitionMock);

        TransitionResult<TestState, TestEvent> actual = lifecycleManager.execute(statefulObject, EVENT);

        assertThat(actual).isNotNull();
    }

    @Test
    @DisplayName("When call LifecycleManagerImpl.execute " +
            "Then TransitionResult.isSucceeded must be true")
    void whenCallExecute_thenTransitionResult$isSucceededMustBeTrue() {
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(transitionProviderMock.getTransition(statefulObject, EVENT)).thenReturn(transitionMock);

        boolean actual = lifecycleManager.execute(statefulObject, EVENT)
                .isSucceeded();

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("Given Transition.transit returns false " +
            "When call LifecycleManagerImpl.execute " +
            "Then TransitionResult.isSucceeded must be false")
    void givenTransition$transitReturnsFalse_whenCallExecute_thenTransitionResult$isSucceededMustBeFalse() {
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(transitionProviderMock.getTransition(statefulObject, EVENT)).thenReturn(transitionMock);
        when(transitionMock.transit(any(StateContext.class))).thenReturn(false);

        boolean actual = lifecycleManager.execute(statefulObject, EVENT)
                .isSucceeded();

        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("Given Transition.transit throws an exception " +
            "When call LifecycleManagerImpl.execute " +
            "Then TransitionResult.isSucceeded must be false")
    void givenTransition$transitThrowsException_whenCallExecute_thenTransitionResult$isSucceededMustBeFalse() {
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(transitionProviderMock.getTransition(statefulObject, EVENT)).thenReturn(transitionMock);
        doThrow(RuntimeException.class).when(transitionMock).transit(any(StateContext.class));

        boolean actual = lifecycleManager.execute(statefulObject, EVENT)
                .isSucceeded();

        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("When call LifecycleManagerImpl.execute " +
            "Then TransitionResult.getSourceState must have the source state")
    void whenCallExecute_thenTransitionResult$getSourceStateMustHaveTheSourceState() {
        final TestState expected = STATE;
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(transitionProviderMock.getTransition(statefulObject, EVENT)).thenReturn(transitionMock);
        when(transitionMock.getSourceState()).thenReturn(expected);

        TestState actual = lifecycleManager.execute(statefulObject, EVENT)
                .getSourceState();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Given Transition.transit returns false " +
            "When call LifecycleManagerImpl.execute " +
            "Then TransitionResult.getSourceState must have the source state")
    void givenTransition$transitReturnsFalse_whenCallExecute_thenTransitionResult$getSourceStateMustHaveTheSourceState() {
        final TestState expected = STATE;
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(transitionProviderMock.getTransition(statefulObject, EVENT)).thenReturn(transitionMock);
        when(transitionMock.getSourceState()).thenReturn(expected);
        when(transitionMock.transit(any(StateContext.class))).thenReturn(false);

        TestState actual = lifecycleManager.execute(statefulObject, EVENT)
                .getSourceState();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Given Transition.transit throws an exception " +
            "When call LifecycleManagerImpl.execute " +
            "Then TransitionResult.getSourceState must have the source state")
    void givenTransition$transitThrowsException_whenCallExecute_thenTransitionResult$getSourceStateMustHaveTheSourceState() {
        final TestState expected = STATE;
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(transitionProviderMock.getTransition(statefulObject, EVENT)).thenReturn(transitionMock);
        when(transitionMock.getSourceState()).thenReturn(expected);
        doThrow(RuntimeException.class).when(transitionMock).transit(any(StateContext.class));

        TestState actual = lifecycleManager.execute(statefulObject, EVENT)
                .getSourceState();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("When call LifecycleManagerImpl.execute " +
            "Then TransitionResult.getTargetState must have the target state")
    void whenCallExecute_thenTransitionResult$getTargetStateMustHaveTheSourceState() {
        final TestState expected = STATE;
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(transitionProviderMock.getTransition(statefulObject, EVENT)).thenReturn(transitionMock);
        when(transitionMock.getTargetState()).thenReturn(expected);

        TestState actual = lifecycleManager.execute(statefulObject, EVENT)
                .getTargetState();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Given Transition.transit returns false " +
            "When call LifecycleManagerImpl.execute " +
            "Then TransitionResult.getTargetState must have the target state")
    void givenTransition$transitReturnsFalse_whenCallExecute_thenTransitionResult$getTargetStateMustHaveTheSourceState() {
        final TestState expected = STATE;
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(transitionProviderMock.getTransition(statefulObject, EVENT)).thenReturn(transitionMock);
        when(transitionMock.getTargetState()).thenReturn(expected);
        when(transitionMock.transit(any(StateContext.class))).thenReturn(false);

        TestState actual = lifecycleManager.execute(statefulObject, EVENT)
                .getTargetState();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Given Transition.transit throws an exception " +
            "When call LifecycleManagerImpl.execute " +
            "Then TransitionResult.getTargetState must have the target state")
    void givenTransition$transitThrowsException_whenCallExecute_thenTransitionResult$getTargetStateMustHaveTheSourceState() {
        final TestState expected = STATE;
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(transitionProviderMock.getTransition(statefulObject, EVENT)).thenReturn(transitionMock);
        when(transitionMock.getTargetState()).thenReturn(expected);
        doThrow(RuntimeException.class).when(transitionMock).transit(any(StateContext.class));

        TestState actual = lifecycleManager.execute(statefulObject, EVENT)
                .getTargetState();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("When call LifecycleManagerImpl.execute " +
            "Then TransitionResult.getStateContext must not be null")
    void whenCallExecute_thenTransitionResult$getStateContextMustNotBeNull() {
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(transitionProviderMock.getTransition(statefulObject, EVENT)).thenReturn(transitionMock);

        StateContext<TestState, TestEvent> actual = lifecycleManager.execute(statefulObject, EVENT)
                .getStateContext();

        assertThat(actual).isNotNull();
    }

    @Test
    @DisplayName("Given Transition.transit returns false " +
            "When call LifecycleManagerImpl.execute " +
            "Then TransitionResult.getStateContext must not be null")
    void givenTransition$transitReturnsFalse_whenCallExecute_thenTransitionResult$getStateContextMustNotBeNull() {
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(transitionProviderMock.getTransition(statefulObject, EVENT)).thenReturn(transitionMock);
        when(transitionMock.transit(any(StateContext.class))).thenReturn(false);

        StateContext<TestState, TestEvent> actual = lifecycleManager.execute(statefulObject, EVENT)
                .getStateContext();

        assertThat(actual).isNotNull();
    }

    @Test
    @DisplayName("Given Transition.transit throws an exception " +
            "When call LifecycleManagerImpl.execute " +
            "Then TransitionResult.getStateContext must not be null")
    void givenTransition$transitThrowsException_whenCallExecute_thenTransitionResult$getStateContextMustNotBeNull() {
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(transitionProviderMock.getTransition(statefulObject, EVENT)).thenReturn(transitionMock);
        doThrow(RuntimeException.class).when(transitionMock).transit(any(StateContext.class));

        StateContext<TestState, TestEvent> actual = lifecycleManager.execute(statefulObject, EVENT)
                .getStateContext();

        assertThat(actual).isNotNull();
    }

    @Test
    @DisplayName("Given Transition.transit throws an exception " +
            "When call LifecycleManagerImpl.execute " +
            "Then TransitionResult.getException must be the exception")
    void givenTransition$transitThrowsException_whenCallExecute_thenTransitionResult$getExceptionMustBeTheException() {
        final Class<RuntimeException> expected = RuntimeException.class;
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(transitionProviderMock.getTransition(statefulObject, EVENT)).thenReturn(transitionMock);
        doThrow(expected).when(transitionMock).transit(any(StateContext.class));

        Exception actual = lifecycleManager.execute(statefulObject, EVENT)
                .getException();

        assertThat(actual).isInstanceOf(expected);
    }

    @Test
    @DisplayName("When call LifecycleManagerImpl.execute " +
            "Then StatefulObject.setState must be called once")
    void whenCallExecute_thenStatefulObject$setTransitionMustBeCalledOnce() {
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(transitionProviderMock.getTransition(statefulObject, EVENT)).thenReturn(transitionMock);
        when(transitionMock.getTargetState()).thenReturn(STATE);

        lifecycleManager.execute(statefulObject, EVENT);

        verify(statefulObject, times(1)).setState(any());
    }

    @Test
    @DisplayName("When call LifecycleManagerImpl.execute " +
            "Then StatefulObject.setState must be called once with state provided by Transition.getTargetState")
    void whenCallExecute_thenStatefulObject$setTransitionMustBeCalledOnceWithStateProvidedByTransition$getTargetState() {
        final TestState expected = STATE;
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(transitionProviderMock.getTransition(statefulObject, EVENT)).thenReturn(transitionMock);
        when(transitionMock.getTargetState()).thenReturn(expected);

        lifecycleManager.execute(statefulObject, EVENT);

        verify(statefulObject, times(1)).setState(expected);
    }

    @Test
    @DisplayName("Given Transition.transit returns false " +
            "When call LifecycleManagerImpl.execute " +
            "Then StatefulObject.setState must not be called")
    void givenTransition$transitReturnsFalse_whenCallExecute_thenStatefulObject$setTransitionMustNotBeCalled() {
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(transitionProviderMock.getTransition(statefulObject, EVENT)).thenReturn(transitionMock);
        when(transitionMock.transit(any(StateContext.class))).thenReturn(false);

        lifecycleManager.execute(statefulObject, EVENT);

        verify(statefulObject, never()).setState(any());
    }

    @Test
    @DisplayName("Given Transition.transit throws an exception " +
            "When call LifecycleManagerImpl.execute " +
            "Then StatefulObject.setState must not be called")
    void givenTransition$transitThrowsException_whenCallExecute_thenStatefulObject$setTransitionMustNotBeCalled() {
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        when(transitionProviderMock.getTransition(statefulObject, EVENT)).thenReturn(transitionMock);
        doThrow(RuntimeException.class).when(transitionMock).transit(any(StateContext.class));

        lifecycleManager.execute(statefulObject, EVENT);

        verify(statefulObject, never()).setState(any());
    }

    @Test
    @DisplayName("Given TransitionProvider.getTransition throws an exception " +
            "When call LifecycleManagerImpl.execute " +
            "Then throws the exception from TransitionProvider")
    void givenTransitionProvider$getTransitionThrowsException_whenCallExecute_thenThrowsExceptionFromTransitionProvider() {
        final Class<AmbiguousTransitionException> expected = AmbiguousTransitionException.class;
        StatefulObject<TestState> statefulObject = mock(StatefulObject.class);
        doThrow(expected).when(transitionProviderMock).getTransition(statefulObject, EVENT);

        assertThatThrownBy(() -> lifecycleManager.execute(statefulObject, EVENT))
                .isInstanceOf(expected);
    }
}