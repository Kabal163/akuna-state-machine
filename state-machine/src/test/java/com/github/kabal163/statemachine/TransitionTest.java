package com.github.kabal163.statemachine;

import com.github.kabal163.statemachine.api.Action;
import com.github.kabal163.statemachine.api.Condition;
import com.github.kabal163.statemachine.api.StateContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

class TransitionTest {

    StateContext<TestState, TestEvent> context;

    Action<TestState, TestEvent> firstAction;
    Action<TestState, TestEvent> secondAction;
    Action<TestState, TestEvent> failedAction;

    Condition<TestState, TestEvent> successCondition;
    Condition<TestState, TestEvent> failedCondition;

    Transition<TestState, TestEvent> transition;

    TransitionTest() {
        context = Mockito.mock(StateContext.class);
        firstAction = Mockito.mock(Action.class);
        secondAction = Mockito.mock(Action.class);
        failedAction = Mockito.mock(Action.class);
        successCondition = Mockito.mock(Condition.class);
        failedCondition = Mockito.mock(Condition.class);

        Mockito.doNothing().when(context).putVariable(any(), any());
        Mockito.doNothing().when(firstAction).execute(context);
        Mockito.doNothing().when(secondAction).execute(context);
        Mockito.when(successCondition.evaluate(context)).thenReturn(true);
        Mockito.when(failedCondition.evaluate(context)).thenReturn(false);
    }

    @BeforeEach
    void setUp() {
        transition = new Transition<>();
    }

    @Test
    @DisplayName("Given one of multiple conditions returns false " +
            "When call Transition.transit " +
            "Then transition is impossible and that's why returns false")
    void givenOneOfMultipleConditionsReturnsFalse_whenCallTransit_thenReturnsFalse() {
        transition.addCondition(failedCondition);
        transition.addCondition(successCondition);
        boolean actual = transition.transit(context);

        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("When call Transition.transit " +
            "Then transition is completed successfully that's why returns true")
    void whenCallTransit_thenReturnsTrue() {
        transition.addAction(firstAction);
        transition.addAction(secondAction);
        boolean actual = transition.transit(context);

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("Given one of multiple actions throws exception " +
            "When call Transition.transit " +
            "Then throws the same exception")
    void givenOneOfMultipleActionsFail_whenCallTransit_thenThrowsRuntimeException() {
        Mockito.doThrow(RuntimeException.class).when(failedAction).execute(context);

        transition.addAction(firstAction);
        transition.addAction(secondAction);
        transition.addAction(failedAction);

        assertThatThrownBy(() -> transition.transit(context))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("When call Transition.transit " +
            "Then then each action must be called once")
    void whenCallTransit_thenEachActionMustBeCalledOnce() {
        transition.addAction(firstAction);
        transition.addAction(secondAction);
        transition.transit(context);

        Mockito.verify(firstAction, Mockito.times(1)).execute(context);
        Mockito.verify(secondAction, Mockito.times(1)).execute(context);
    }

    @Test
    @DisplayName("When call Transition.addAction " +
            "Then actions must be sorted according the order they were added")
    void whenCallAddActionMultipleTimes_thenActionsMustBeOrderedAsTheyWereAdded() {
        Action<TestState, TestEvent> thirdAction = Mockito.mock(Action.class);
        Action<TestState, TestEvent> fourthAction = Mockito.mock(Action.class);

        List<Action<TestState, TestEvent>> expected = List.of(firstAction, secondAction, thirdAction, fourthAction);
        for (Action<TestState, TestEvent> action : expected) {
            transition.addAction(action);
        }

        List<Action<TestState, TestEvent>> actual = new ArrayList<>(transition.getActions());

        assertThat(actual)
                .isSortedAccordingTo(Comparator.comparingInt(expected::indexOf));
    }
}