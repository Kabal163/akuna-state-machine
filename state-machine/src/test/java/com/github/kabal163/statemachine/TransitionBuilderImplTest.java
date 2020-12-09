package com.github.kabal163.statemachine;

import com.github.kabal163.statemachine.api.Action;
import com.github.kabal163.statemachine.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.github.kabal163.statemachine.TestEvent.EVENT;
import static com.github.kabal163.statemachine.TestState.ANOTHER_STATE;
import static com.github.kabal163.statemachine.TestState.STATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransitionBuilderImplTest {

    TransitionBuilderImpl<TestState, TestEvent> transitionBuilder;

    //@formatted:off
    @Mock Action<TestState, TestEvent> action;
    @Mock Condition<TestState, TestEvent> condition;
    //@formatted:on

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        transitionBuilder = new TransitionBuilderImpl<>();
    }

    @ParameterizedTest
    @MethodSource("getTransitionBuildersWithIncompleteTransitions")
    @DisplayName("Given incomplete transition configuration " +
            "When call TransitionBuilderImpl.buildTransitions " +
            "Then throws IllegalStateException")
    void givenIncompleteTransitionConfiguration_whenCallBuildTransitions_thenIllegalStateException(TransitionBuilderImpl<TestState, TestEvent> transitionBuilder) {
        assertThatThrownBy(transitionBuilder::buildTransitions)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Transition must have mandatory parameters");
    }

    @Test
    @DisplayName("When call TransitionBuilderImpl.buildTransitions " +
            "Then returns not null")
    void whenCallBuildTransitions_thenReturnsNotNull() {
        transitionBuilder
                .with()
                .sourceState(STATE)
                .targetState(ANOTHER_STATE)
                .event(EVENT)
                .condition(condition)
                .action(action);
        Set<Transition<TestState, TestEvent>> actual = transitionBuilder.buildTransitions();

        assertThat(actual).isNotNull();
    }

    @Test
    @DisplayName("Given one configured transition " +
            "When call TransitionBuilderImpl.buildTransitions " +
            "Then returns collection with size = 1")
    void givenOneConfiguredTransition_whenCallBuildTransitions_thenReturnsCollectionOfSizeOne() {
        transitionBuilder
                .with()
                .sourceState(STATE)
                .targetState(ANOTHER_STATE)
                .event(EVENT)
                .condition(condition)
                .action(action);
        Set<Transition<TestState, TestEvent>> actual = transitionBuilder.buildTransitions();

        assertThat(actual).hasSize(1);
    }

    @Test
    @DisplayName("Given configured transition with source state " +
            "When call TransitionBuilderImpl.buildTransitions " +
            "Then returns transition with the same source state")
    void givenConfiguredTransitionWithSourceState_whenCallBuildTransitions_thenReturnsTransitionWithTheSameSourceState() {
        final TestState expected = STATE;
        transitionBuilder
                .with()
                .sourceState(expected)
                .targetState(ANOTHER_STATE)
                .event(EVENT)
                .condition(condition)
                .action(action);
        Transition<TestState, TestEvent> actual = transitionBuilder.buildTransitions()
                .stream()
                .findFirst()
                .orElseThrow();

        assertThat(actual.getSourceState()).isEqualTo(expected);
    }

    @Test
    @DisplayName("Given configured transition with target state " +
            "When call TransitionBuilderImpl.buildTransitions " +
            "Then returns transition with the same target state")
    void givenConfiguredTransitionWithTargetState_whenCallBuildTransitions_thenReturnsTransitionWithTheSameTargetState() {
        final TestState expected = ANOTHER_STATE;
        transitionBuilder
                .with()
                .sourceState(STATE)
                .targetState(expected)
                .event(EVENT)
                .condition(condition)
                .action(action);
        Transition<TestState, TestEvent> actual = transitionBuilder.buildTransitions()
                .stream()
                .findFirst()
                .orElseThrow();

        assertThat(actual.getTargetState()).isEqualTo(expected);
    }

    @Test
    @DisplayName("Given configured transition with event " +
            "When call TransitionBuilderImpl.buildTransitions " +
            "Then returns transition with the same event")
    void givenConfiguredTransitionWithEvent_whenCallBuildTransitions_thenReturnsTransitionWithTheSameEvent() {
        final TestEvent expected = EVENT;
        transitionBuilder
                .with()
                .sourceState(STATE)
                .targetState(ANOTHER_STATE)
                .event(expected)
                .condition(condition)
                .action(action);
        Transition<TestState, TestEvent> actual = transitionBuilder.buildTransitions()
                .stream()
                .findFirst()
                .orElseThrow();

        assertThat(actual.getEvent()).isEqualTo(expected);
    }

    @Test
    @DisplayName("Given configured transition with one condition " +
            "When call TransitionBuilderImpl.buildTransitions " +
            "Then returns transition with collection of conditions having size = 1")
    void givenConfiguredTransitionWithOneCondition_whenCallBuildTransitions_thenReturnsTransitionWithCollectionOfConditionsOfSizeOne() {
        transitionBuilder
                .with()
                .sourceState(STATE)
                .targetState(ANOTHER_STATE)
                .event(EVENT)
                .condition(condition)
                .action(action);
        Transition<TestState, TestEvent> actual = transitionBuilder.buildTransitions()
                .stream()
                .findFirst()
                .orElseThrow();

        assertThat(actual.getConditions()).hasSize(1);
    }

    @Test
    @DisplayName("Given configured transition with one action " +
            "When call TransitionBuilderImpl.buildTransitions " +
            "Then returns transition with collection of actions having size = 1")
    void givenConfiguredTransitionWithOneAction_whenCallBuildTransitions_thenReturnsTransitionWithCollectionOfActionsOfSizeOne() {
        transitionBuilder
                .with()
                .sourceState(STATE)
                .targetState(ANOTHER_STATE)
                .event(EVENT)
                .condition(condition)
                .action(action);
        Transition<TestState, TestEvent> actual = transitionBuilder.buildTransitions()
                .stream()
                .findFirst()
                .orElseThrow();

        assertThat(actual.getActions()).hasSize(1);
    }

    @Test
    @DisplayName("Given configured transition with one condition " +
            "When call TransitionBuilderImpl.buildTransitions " +
            "Then returns transition with collection of conditions containing the same condition")
    void givenConfiguredTransitionWithOneCondition_whenCallBuildTransitions_thenReturnsTransitionWithCollectionContainingTheCondition() {
        final Condition<TestState, TestEvent> expected = condition;
        transitionBuilder
                .with()
                .sourceState(STATE)
                .targetState(ANOTHER_STATE)
                .event(EVENT)
                .condition(expected)
                .action(action);
        Condition<TestState, TestEvent> actual = transitionBuilder.buildTransitions()
                .stream()
                .findFirst()
                .orElseThrow()
                .getConditions()
                .stream()
                .findFirst()
                .orElseThrow();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Given configured transition with one action " +
            "When call TransitionBuilderImpl.buildTransitions " +
            "Then returns transition with collection of actions containing the same action")
    void givenConfiguredTransitionWithOneAction_whenCallBuildTransitions_thenReturnsTransitionWithCollectionContainingTheAction() {
        final Action<TestState, TestEvent> expected = action;
        transitionBuilder
                .with()
                .sourceState(STATE)
                .targetState(ANOTHER_STATE)
                .event(EVENT)
                .condition(condition)
                .action(expected);
        Action<TestState, TestEvent> actual = transitionBuilder.buildTransitions()
                .stream()
                .findFirst()
                .orElseThrow()
                .getActions()
                .stream()
                .findFirst()
                .orElseThrow();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Given five actions added with particular order " +
            "When call TransitionBuilderImpl.buildTransitions " +
            "Then returns transition with collection of actions which has the same order")
    void givenFiveActionsAddedWithParticularOrder_whenCallBuildTransitions_thenReturnsTransitionWithActionsWhichHaveTheSameOrder() {
        final List<Action<TestState, TestEvent>> expected = List.of(
                Mockito.mock(Action.class),
                Mockito.mock(Action.class),
                Mockito.mock(Action.class),
                Mockito.mock(Action.class),
                Mockito.mock(Action.class)
        );

        transitionBuilder
                .with()
                .sourceState(STATE)
                .targetState(ANOTHER_STATE)
                .event(EVENT)
                .condition(this.condition);
        expected.forEach(transitionBuilder::action);

        List<Action<TestState, TestEvent>> actual = transitionBuilder.buildTransitions().stream()
                .findFirst()
                .orElseThrow()
                .getActions();

        assertThat(actual)
                .isSortedAccordingTo(Comparator.comparingInt(expected::indexOf));
    }

    @Test
    @DisplayName("Given forget to call method TransitionBuilderImpl.with before describing a transition " +
            "When call TransitionBuilderImpl.sourceState " +
            "Then throws IllegalStateException")
    void givenMethodWithIsNotCalled_whenCallSourceState_thenThrowsIllegalStateException() {
        assertThatThrownBy(() -> transitionBuilder.sourceState(STATE))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Given forget to call method TransitionBuilderImpl.with before describing a transition " +
            "When call TransitionBuilderImpl.targetState " +
            "Then throws IllegalStateException")
    void givenMethodWithIsNotCalled_whenCallTargetState_thenThrowsIllegalStateException() {
        assertThatThrownBy(() -> transitionBuilder.targetState(ANOTHER_STATE))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Given forget to call method TransitionBuilderImpl.with before describing a transition " +
            "When call TransitionBuilderImpl.event " +
            "Then throws IllegalStateException")
    void givenMethodWithIsNotCalled_whenCallEvent_thenThrowsIllegalStateException() {
        assertThatThrownBy(() -> transitionBuilder.event(EVENT))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Given forget to call method TransitionBuilderImpl.with before describing a transition " +
            "When call TransitionBuilderImpl.condition " +
            "Then throws IllegalStateException")
    void givenMethodWithIsNotCalled_whenCallCondition_thenThrowsIllegalStateException() {
        assertThatThrownBy(() -> transitionBuilder.condition(condition))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Given forget to call method TransitionBuilderImpl.with before describing a transition " +
            "When call TransitionBuilderImpl.action " +
            "Then throws IllegalStateException")
    void givenMethodWithIsNotCalled_whenCallAction_thenThrowsIllegalStateException() {
        assertThatThrownBy(() -> transitionBuilder.action(action))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Given state is null " +
            "When call TransitionBuilderImpl.sourceState " +
            "Then throws NullPointerException")
    void givenNullSourceState_whenCallSourceState_thenThrowsNullPointerException() {
        assertThatThrownBy(() -> transitionBuilder.sourceState(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("must not be null");
    }

    @Test
    @DisplayName("Given state is null " +
            "When call TransitionBuilderImpl.targetState " +
            "Then throws NullPointerException")
    void givenNullSourceState_whenCallTargetState_thenThrowsNullPointerException() {
        assertThatThrownBy(() -> transitionBuilder.targetState(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("must not be null");
    }

    @Test
    @DisplayName("Given event is null " +
            "When call TransitionBuilderImpl.event " +
            "Then throws NullPointerException")
    void givenNullSourceState_whenCallEvent_thenThrowsNullPointerException() {
        assertThatThrownBy(() -> transitionBuilder.event(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("must not be null");
    }

    @Test
    @DisplayName("Given action is null " +
            "When call TransitionBuilderImpl.action " +
            "Then throws NullPointerException")
    void givenNullSourceState_whenCallAction_thenThrowsNullPointerException() {
        assertThatThrownBy(() -> transitionBuilder.action(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("must not be null");
    }

    @Test
    @DisplayName("Given condition is null " +
            "When call TransitionBuilderImpl.condition " +
            "Then throws NullPointerException")
    void givenNullSourceState_whenCallCondition_thenThrowsNullPointerException() {
        assertThatThrownBy(() -> transitionBuilder.condition(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("must not be null");
    }

    /**
     * For more details information about required params see {@link TransitionConfigurer}
     * @see TransitionConfigurer
     */
    public static Stream<Arguments> getTransitionBuildersWithIncompleteTransitions() {
        return Stream.of(
                Arguments.of(new TransitionBuilderImpl<>().with()),
                Arguments.of(new TransitionBuilderImpl<>().with().sourceState(STATE).targetState(EVENT)),
                Arguments.of(new TransitionBuilderImpl<>().with().sourceState(STATE).event(EVENT)),
                Arguments.of(new TransitionBuilderImpl<>().with().targetState(ANOTHER_STATE).event(EVENT))
        );
    }
}