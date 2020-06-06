package com.github.kabal163.statemachine;

import com.github.kabal163.statemachine.api.Action;
import com.github.kabal163.statemachine.api.Condition;
import com.github.kabal163.statemachine.api.StateContext;
import com.github.kabal163.statemachine.testimpl.Event;
import com.github.kabal163.statemachine.testimpl.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

class TransitionTest {

    StateContext<State, Event> context;

    Action<State, Event> firstAction;
    Action<State, Event> secondAction;
    Action<State, Event> failedAction;

    Condition<State, Event> successCondition;
    Condition<State, Event> failedCondition;

    Transition<State, Event> transition;

    @SuppressWarnings("unchecked")
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
        Mockito.doThrow(RuntimeException.class).when(failedAction).execute(context);
        Mockito.when(successCondition.evaluate(context)).thenReturn(true);
        Mockito.when(failedCondition.evaluate(context)).thenReturn(false);
    }

    @BeforeEach
    void setUp() {
        transition = new Transition<>();
    }

    @Test
    void falseConditionPreventsTransition() {
        transition.addCondition(failedCondition);
        boolean actualResult = transition.transit(context);

        Assertions.assertFalse(actualResult);
    }

    @Test
    void successConditionAllowTransition() {
        transition.addCondition(successCondition);
        boolean actualResult = transition.transit(context);

        Assertions.assertTrue(actualResult);
    }

    @Test
    void anyFalseConditionPreventsTransition() {
        transition.addCondition(failedCondition);
        transition.addCondition(successCondition);
        boolean actualResult = transition.transit(context);

        Assertions.assertFalse(actualResult);
    }

    @Test
    void transitionIsSuccessfulIfAllActionsAreSuccessful() {
        transition.addAction(firstAction);
        transition.addAction(secondAction);
        boolean actualResult = transition.transit(context);

        Assertions.assertTrue(actualResult);
    }

    @Test
    void transitionThrowsExceptionIfAnyActionIsFailed() {
        transition.addAction(firstAction);
        transition.addAction(secondAction);
        transition.addAction(failedAction);

        Assertions.assertThrows(RuntimeException.class, () -> transition.transit(context));
    }

    @Test
    void eachActionMustBeInvoked() {
        transition.addAction(firstAction);
        transition.addAction(secondAction);
        transition.transit(context);

        Mockito.verify(firstAction, Mockito.times(1)).execute(context);
        Mockito.verify(secondAction, Mockito.times(1)).execute(context);
    }

    @Test
    @SuppressWarnings("unchecked")
    void actionsOrderMustBeTheSameAsOnConfiguration() {
        Action<State, Event> thirdAction = Mockito.mock(Action.class);
        Action<State, Event> fourthAction = Mockito.mock(Action.class);

        transition.addAction(firstAction);
        transition.addAction(secondAction);
        transition.addAction(thirdAction);
        transition.addAction(fourthAction);

        List<Action<State, Event>> expectedActions = List.of(firstAction, secondAction, thirdAction, fourthAction);
        List<Action<State, Event>> actualActions = new ArrayList<>(transition.getActions());

        boolean result = IntStream.range(0, 4)
                .allMatch(i -> Objects.equals(expectedActions.get(i), actualActions.get(i)));

        assertTrue(result);
    }
}