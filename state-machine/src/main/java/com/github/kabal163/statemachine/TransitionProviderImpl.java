package com.github.kabal163.statemachine;

import com.github.kabal163.statemachine.api.LifecycleConfiguration;
import com.github.kabal163.statemachine.api.StatefulObject;
import com.github.kabal163.statemachine.api.TransitionsInitializer;
import com.github.kabal163.statemachine.exception.AmbiguousTransitionException;
import com.github.kabal163.statemachine.exception.TransitionNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class TransitionProviderImpl implements TransitionProvider {

    private static final Logger log = LoggerFactory.getLogger(TransitionProviderImpl.class);
    private final Map<String, Set<Transition>> transitions;

    private TransitionProviderImpl(Map<String, Set<Transition>> transitions) {
        this.transitions = transitions;
    }

    public static TransitionProviderBuilder builder() {
        return new TransitionProviderBuilder();
    }

    @Override
    public Transition getTransition(StatefulObject statefulObject, String event) {
        String sourceState = statefulObject.getState();
        assertLifecycleIsSupported(statefulObject);

        Set<Transition> matchTransitions = transitions.get(statefulObject.getLifecycleName()).stream()
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

    private void assertLifecycleIsSupported(StatefulObject statefulObject) {
        if (!transitions.containsKey(statefulObject.getLifecycleName())) {
            log.error("There is no such lifecycle: {}", statefulObject.getLifecycleName());
            throw new IllegalStateException("There is no such lifecycle: " + statefulObject.getLifecycleName());
        }
    }

    public static final class TransitionProviderBuilder {

        private List<LifecycleConfiguration> javaConfigs;
        private TransitionsInitializer transitionsInitializer;

        public TransitionProviderBuilder configs(List<LifecycleConfiguration> configurations) {
            this.javaConfigs = configurations;
            return this;
        }

        public TransitionProviderBuilder transitionInitializer(TransitionsInitializer transitionsInitializer) {
            this.transitionsInitializer = transitionsInitializer;
            return this;
        }

        public TransitionProviderImpl build() {
            if (javaConfigs == null || javaConfigs.isEmpty()) {
                throw new IllegalArgumentException("Configurations must not be null nor empty!");
            }

            if (transitionsInitializer == null) {
                transitionsInitializer = new JavaConfigTransitionsInitializer();
            }

            return new TransitionProviderImpl(transitionsInitializer.initialize(javaConfigs));
        }
    }
}
