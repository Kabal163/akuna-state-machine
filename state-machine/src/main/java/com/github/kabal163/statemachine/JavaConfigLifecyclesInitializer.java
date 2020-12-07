package com.github.kabal163.statemachine;

import com.github.kabal163.statemachine.api.LifecycleConfiguration;
import com.github.kabal163.statemachine.api.LifecyclesInitializer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Builds transitions from java configurations.
 * Transitions are grouped by lifecycle name declared
 * in the configurations.
 */
public class JavaConfigLifecyclesInitializer implements LifecyclesInitializer {

    @Override
    public <S, E> Map<String, Lifecycle<S, E>> initialize(Collection<LifecycleConfiguration<S, E>> configurations) {
        Objects.requireNonNull(configurations, "Configurations must not be null!");
        if (configurations.isEmpty()) throw new IllegalArgumentException("Configurations must not be empty!");

        Map<String, Lifecycle<S, E>> lifecyclesByName = new HashMap<>();
        for (LifecycleConfiguration<S, E> configuration : configurations) {
            TransitionBuilder<S, E> configurer = new TransitionBuilderImpl<>();
            configuration.configureTransitions(configurer);
            String lifecycleName = isBlank(configuration.getLifecycleName())
                    ? configuration.getClass().getCanonicalName()
                    : configuration.getLifecycleName();
            Lifecycle<S, E> lifecycle = LifecycleImpl.<S, E>builder()
                    .name(lifecycleName)
                    .transitions(configurer.buildTransitions())
                    .build();

            lifecyclesByName.put(lifecycleName, lifecycle);
        }

        return lifecyclesByName;
    }
}
