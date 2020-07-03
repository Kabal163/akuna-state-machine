package com.github.kabal163.statemachine;

import com.github.kabal163.statemachine.api.LifecycleConfiguration;
import com.github.kabal163.statemachine.api.LifecycleManager;
import com.github.kabal163.statemachine.api.StateContext;
import com.github.kabal163.statemachine.api.StatefulObject;
import com.github.kabal163.statemachine.api.TransitionResult;
import com.github.kabal163.statemachine.exception.AmbiguousTransitionException;
import com.github.kabal163.statemachine.exception.TransitionNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class LifecycleManagerImpl implements LifecycleManager {

    private final TransitionBuilder transitionBuilder;
    private final LifecycleConfiguration lifecycleConfiguration;

    private Set<Transition> transitions;

    public void init() {
        lifecycleConfiguration.configureTransitions(transitionBuilder);
        transitions = transitionBuilder.buildTransitions();
    }

    @Override
    public TransitionResult execute(StatefulObject statefulObject, String event) {
        return execute(statefulObject, event, new HashMap<>());
    }

    @Override
    public TransitionResult execute(StatefulObject statefulObject, String event, Map<String, Object> variables) {
        Transition transition = getMatchingTransition(statefulObject, event);
        StateContext context = new StateContext(statefulObject, event, variables);
        boolean success = false;
        Exception exception = null;

        try {
            success = transition.transit(context);
        } catch (Exception ex) {
            log.error("Error while transition from {} to {} with event {}; id: {}",
                    transition.getSourceState(),
                    transition.getTargetState(),
                    event,
                    statefulObject.getId(),
                    ex);
            exception = ex;
        }

        if (success) {
            statefulObject.setState(transition.getTargetState());
        }

        return new TransitionResult(
                success,
                context,
                transition.getSourceState(),
                transition.getTargetState(),
                exception);
    }

    private Transition getMatchingTransition(StatefulObject statefulObject, String event) {
        String sourceState = statefulObject.getState();

        Set<Transition> matchTransitions = transitions.stream()
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
