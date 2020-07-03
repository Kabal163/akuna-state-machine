package com.github.kabal163.statemachine;

import com.github.kabal163.statemachine.api.Action;
import com.github.kabal163.statemachine.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransitionBuilderImplTest {

    TransitionBuilderImpl transitionBuilder;

    Action action;
    Condition condition;

    TransitionBuilderImplTest() {
        action = Mockito.mock(Action.class);
        condition = Mockito.mock(Condition.class);
    }

    @BeforeEach
    void setUp() {
        transitionBuilder = new TransitionBuilderImpl();
    }

    @ParameterizedTest
    @MethodSource("getTransitionBuildersWithIncompleteTransitions")
    void exceptionDueToIncompleteTransition(TransitionBuilderImpl transitionBuilder) {
        assertThrows(IllegalStateException.class, transitionBuilder::buildTransitions);
    }

    @Test
    void successfulTransitionsBuild() {
        transitionBuilder
                .with()
                .sourceState("NEW")
                .targetState("APPROVED")
                .event("APPROVE")
                .condition(condition)
                .action(action);
        Set<Transition> transitions = transitionBuilder.buildTransitions();

        assertEquals(1, transitions.size());

        Transition transition = transitions.stream().findFirst().orElseThrow();
        assertEquals("NEW", transition.getSourceState());
        assertEquals("APPROVED", transition.getTargetState());
        assertEquals("APPROVE", transition.getEvent());
        assertEquals(1, transition.getConditions().size());
        assertEquals(1, transition.getActions().size());

        Condition actualCondition = transition.getConditions().stream().findFirst().orElseThrow();
        Action actualAction = transition.getActions().stream().findFirst().orElseThrow();

        assertEquals(condition, actualCondition);
        assertEquals(action, actualAction);
    }

    @Test
    void exceptionDueToSkippingWithMethodInStartOfConfiguration() {
        assertThrows(IllegalStateException.class, () -> transitionBuilder.sourceState("NEW"));
    }

    @Test
    void exceptionDueToNullAction() {
        assertThrows(IllegalArgumentException.class, () -> transitionBuilder.with().action(null));
    }

    @Test
    void exceptionDueToNullCondition() {
        assertThrows(IllegalArgumentException.class, () -> transitionBuilder.with().condition(null));
    }

    public static Stream<Arguments> getTransitionBuildersWithIncompleteTransitions() {
        return Stream.of(
                Arguments.of(new TransitionBuilderImpl().with()),
                Arguments.of(new TransitionBuilderImpl().with().sourceState("NEW").targetState("APPROVED")),
                Arguments.of(new TransitionBuilderImpl().with().sourceState("NEW").event("APPROVE")),
                Arguments.of(new TransitionBuilderImpl().with().targetState("APPROVED").event("APPROVE"))
        );
    }

}