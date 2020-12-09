package com.github.kabal163.statemachine;

import com.github.kabal163.statemachine.api.Action;
import com.github.kabal163.statemachine.api.Condition;
import com.github.kabal163.statemachine.api.StateContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static com.github.kabal163.statemachine.TestEvent.EVENT;
import static com.github.kabal163.statemachine.TestState.ANOTHER_STATE;
import static com.github.kabal163.statemachine.TestState.STATE;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

class TransitionTest {

    //@formatter:off
    @Mock StateContext<TestState, TestEvent> context;

    final TestState anySourceState = STATE;
    final TestState anyTargetState = ANOTHER_STATE;
    final TestEvent anyEvent = EVENT;

    @Mock Action<TestState, TestEvent> firstAction;
    @Mock Action<TestState, TestEvent> secondAction;
    @Mock Action<TestState, TestEvent> failedAction;

    @Mock Condition<TestState, TestEvent> successCondition;
    @Mock Condition<TestState, TestEvent> failedCondition;
    //@formatter:on

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        Mockito.doNothing().when(context).putVariable(any(), any());
        Mockito.doNothing().when(firstAction).execute(context);
        Mockito.doNothing().when(secondAction).execute(context);
        Mockito.when(successCondition.evaluate(context)).thenReturn(true);
        Mockito.when(failedCondition.evaluate(context)).thenReturn(false);
    }

    @Test
    @DisplayName("Given source state is null " +
            "When create new Transition " +
            "Then throws NullPointerException")
    void givenSourceStateIsNull_whenCreateTransition_thenThrowsNullPointerException() {
        Set<Condition<TestState, TestEvent>> emptySet = emptySet();
        List<Action<TestState, TestEvent>> emptyList = emptyList();
        assertThatThrownBy(() -> new Transition<>(null, anyTargetState, anyEvent, emptySet, emptyList))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("must not be null");
    }

    @Test
    @DisplayName("Given target state is null " +
            "When create new Transition " +
            "Then throws NullPointerException")
    void givenTargetStateIsNull_whenCreateTransition_thenThrowsNullPointerException() {
        Set<Condition<TestState, TestEvent>> emptySet = emptySet();
        List<Action<TestState, TestEvent>> emptyList = emptyList();
        assertThatThrownBy(() -> new Transition<>(anySourceState, null, anyEvent, emptySet, emptyList))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("must not be null");
    }

    @Test
    @DisplayName("Given event is null " +
            "When create new Transition " +
            "Then throws NullPointerException")
    void givenEventIsNull_whenCreateTransition_thenThrowsNullPointerException() {
        Set<Condition<TestState, TestEvent>> emptySet = emptySet();
        List<Action<TestState, TestEvent>> emptyList = emptyList();
        assertThatThrownBy(() -> new Transition<>(anySourceState, anyTargetState, null, emptySet, emptyList))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("must not be null");
    }

    @Test
    @DisplayName("Given conditions is null " +
            "When create new Transition " +
            "Then throws NullPointerException")
    void givenConditionsIsNull_whenCreateTransition_thenThrowsNullPointerException() {
        List<Action<TestState, TestEvent>> emptyList = emptyList();
        assertThatThrownBy(() -> new Transition<>(anySourceState, anyTargetState, anyEvent, null, emptyList))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("must not be null");
    }

    @Test
    @DisplayName("Given actions is null " +
            "When create new Transition " +
            "Then throws NullPointerException")
    void givenActionsIsNull_whenCreateTransition_thenThrowsNullPointerException() {
        Set<Condition<TestState, TestEvent>> emptySet = emptySet();
        assertThatThrownBy(() -> new Transition<>(anySourceState, anyTargetState, anyEvent, emptySet, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("must not be null");
    }

    @Test
    @DisplayName("Given one of multiple conditions returns false " +
            "When call Transition.transit " +
            "Then transition is impossible and that's why returns false")
    void givenOneOfMultipleConditionsReturnsFalse_whenCallTransit_thenReturnsFalse() {
        Transition<TestState, TestEvent> transition = new Transition<>(
                anySourceState,
                anyTargetState,
                anyEvent,
                Set.of(successCondition, failedCondition),
                emptyList());
        boolean actual = transition.transit(context);

        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("When call Transition.transit " +
            "Then transition is completed successfully that's why returns true")
    void whenCallTransit_thenReturnsTrue() {
        Transition<TestState, TestEvent> transition = new Transition<>(
                anySourceState,
                anyTargetState,
                anyEvent,
                Set.of(successCondition),
                List.of(firstAction, secondAction));
        boolean actual = transition.transit(context);

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("Given one of multiple actions throws exception " +
            "When call Transition.transit " +
            "Then throws the same exception")
    void givenOneOfMultipleActionsFail_whenCallTransit_thenThrowsRuntimeException() {
        Mockito.doThrow(RuntimeException.class).when(failedAction).execute(context);

        Transition<TestState, TestEvent> transition = new Transition<>(
                anySourceState,
                anyTargetState,
                anyEvent,
                emptySet(),
                List.of(firstAction, secondAction, failedAction));

        assertThatThrownBy(() -> transition.transit(context))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("When call Transition.transit " +
            "Then then each action must be called once")
    void whenCallTransit_thenEachActionMustBeCalledOnce() {
        Transition<TestState, TestEvent> transition = new Transition<>(
                anySourceState,
                anyTargetState,
                anyEvent,
                emptySet(),
                List.of(firstAction, secondAction));

        transition.transit(context);

        Mockito.verify(firstAction, Mockito.times(1)).execute(context);
        Mockito.verify(secondAction, Mockito.times(1)).execute(context);
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("When create Transition " +
            "Then actions must save the initial order")
    void whenCreateTransition_thenActionsMustSaveInitialOrder() {
        Action<TestState, TestEvent> thirdAction = Mockito.mock(Action.class);
        Action<TestState, TestEvent> fourthAction = Mockito.mock(Action.class);

        List<Action<TestState, TestEvent>> expected = List.of(firstAction, secondAction, thirdAction, fourthAction);
        Transition<TestState, TestEvent> transition = new Transition<>(
                anySourceState,
                anyTargetState,
                anyEvent,
                emptySet(),
                expected);

        List<Action<TestState, TestEvent>> actual = transition.getActions();

        assertThat(actual)
                .isSortedAccordingTo(Comparator.comparingInt(expected::indexOf));
    }
}