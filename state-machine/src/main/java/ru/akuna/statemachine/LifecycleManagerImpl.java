package ru.akuna.statemachine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.akuna.statemachine.api.ContextConstants;
import ru.akuna.statemachine.api.LifecycleConfiguration;
import ru.akuna.statemachine.api.LifecycleManager;
import ru.akuna.statemachine.api.StateContext;
import ru.akuna.statemachine.api.StatefulObject;
import ru.akuna.statemachine.api.TransitionResult;
import ru.akuna.statemachine.exception.AmbiguousTransitionException;
import ru.akuna.statemachine.exception.TransitionNotFoundException;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptyMap;
import static java.util.Optional.ofNullable;

@Slf4j
@RequiredArgsConstructor
public class LifecycleManagerImpl<S, E> implements LifecycleManager<S, E> {

    private final TransitionBuilder<S, E> transitionBuilder;
    private final LifecycleConfiguration<S, E> lifecycleConfiguration;

    private Set<Transition<S, E>> transitions;

    public void init() {
        lifecycleConfiguration.configureTransitions(transitionBuilder);
        transitions = transitionBuilder.buildTransitions();
    }

    @Override
    public TransitionResult<S, E> execute(StatefulObject<S> statefulObject, E event) {
        return execute(statefulObject, event, emptyMap());
    }

    @Override
    public TransitionResult<S, E> execute(StatefulObject<S> statefulObject, E event, Map<String, Object> variables) {
        Transition<S, E> transition = getMatchingTransition(statefulObject, event);
        StateContext<S, E> context = new StateContext<>(statefulObject, event, variables);
        boolean success = false;

        try {
            success = transition.transit(context);
        } catch (Exception ex) {
            log.error("Error while transition from {} to {} with event {}; id: {}",
                    transition.getSourceState(),
                    transition.getTargetState(),
                    event,
                    statefulObject.getId(),
                    ex);

            context.putIfAbsentVariable(
                    ContextConstants.ERROR_MESSAGE,
                    ofNullable(ex.getMessage())
                            .orElse(ex.getClass().getCanonicalName()));
        }

        if (success) {
            statefulObject.setState(transition.getTargetState());
        }

        return new TransitionResult<>(
                success,
                context,
                transition.getSourceState(),
                transition.getTargetState());
    }

    private Transition<S, E> getMatchingTransition(StatefulObject<S> statefulObject, E event) {
        S sourceState = statefulObject.getState();

        Set<Transition<S, E>> matchTransitions = transitions.stream()
                .filter(t -> Objects.equals(t.getSourceState(), sourceState))
                .filter(t -> Objects.equals(t.getEvent(), event))
                .collect(Collectors.toSet());

        if (matchTransitions.size() > 1) {
            log.error("There is more then one transition match! Matching transitions: {}",
                    matchTransitions.stream()
                            .map(t -> t.getClass().getName())
                            .toArray());
            throw new AmbiguousTransitionException("There is more then one transition match!");
        }

        return matchTransitions.stream()
                .findFirst()
                .orElseThrow(() -> {
                    log.error("There is no matching transition for source state: {} and event: {}, id: {}",
                            sourceState,
                            event,
                            statefulObject.getId());
                    return new TransitionNotFoundException("There is no matching transition!");
                });
    }
}
