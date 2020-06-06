package com.github.kabal163.statemachine;

import com.github.kabal163.statemachine.api.Action;
import com.github.kabal163.statemachine.api.Condition;
import com.github.kabal163.statemachine.api.StateContext;
import com.github.kabal163.statemachine.testimpl.Event;
import com.github.kabal163.statemachine.testimpl.State;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
}