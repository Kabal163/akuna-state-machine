package com.github.kabal163.statemachine;

import com.github.kabal163.statemachine.api.LifecycleConfiguration;
import com.github.kabal163.statemachine.api.TransitionsInitializer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class JavaConfigTransitionsInitializer implements TransitionsInitializer {

    @Override
    public Map<String, Set<Transition>> initialize(Collection<LifecycleConfiguration> configurations) {
        assertConfigurationsNotNullNotEmpty(configurations);
        Map<String, Set<Transition>> transitions = new HashMap<>();

        for (LifecycleConfiguration configuration : configurations) {
            TransitionBuilder configurer = new TransitionBuilderImpl();
            configuration.configureTransitions(configurer);
            String lifecycleName = isBlank(configuration.getLifecycleName())
                    ? configuration.getClass().getCanonicalName()
                    : configuration.getLifecycleName();

            transitions.put(lifecycleName, configurer.buildTransitions());
        }

        return transitions;
    }

    private void assertConfigurationsNotNullNotEmpty(Collection<LifecycleConfiguration> configurations) {
        if (configurations == null || configurations.isEmpty()) {
            throw new IllegalArgumentException("Configurations must not be null nor empty!");
        }
    }
}
