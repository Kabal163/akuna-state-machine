package com.github.kabal163.statemachine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.kabal163.statemachine.api.StatefulObject;
import com.github.kabal163.statemachine.exception.AmbiguousTransitionException;
import com.github.kabal163.statemachine.exception.LifecycleNotFoundException;
import com.github.kabal163.statemachine.exception.TransitionNotFoundException;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class TransitionProviderImpl<S, E> implements TransitionProvider<S, E> {

    private static final Logger log = LoggerFactory.getLogger(TransitionProviderImpl.class);

    private final Map<String, Lifecycle<S, E>> lifecyclesByName;

    public TransitionProviderImpl(Map<String, Lifecycle<S, E>> lifecyclesByName) {
        this.lifecyclesByName = lifecyclesByName;
    }

    @Override
    public Transition<S, E> getTransition(StatefulObject<S> statefulObject, E event) {
        Objects.requireNonNull(statefulObject, "StatefulObject must not be null!");
        Objects.requireNonNull(event, "event must not be null!");
        assertLifecycleIsSupported(statefulObject);

        S sourceState = statefulObject.getState();
        Set<Transition<S, E>> matchTransitions = lifecyclesByName
                .get(statefulObject.getLifecycleName())
                .getTransitions()
                .stream()
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

    private void assertLifecycleIsSupported(StatefulObject<S> statefulObject) {
        if (isBlank(statefulObject.getLifecycleName())) {
            log.error("Null or empty lifecycle names are not supported!");
            throw new LifecycleNotFoundException("Null or empty lifecycle names are not supported!");
        }
        if (!lifecyclesByName.containsKey(statefulObject.getLifecycleName())) {
            log.error("There is no such lifecycle: {}", statefulObject.getLifecycleName());
            throw new LifecycleNotFoundException("There is no such lifecycle: " + statefulObject.getLifecycleName());
        }
    }
}
