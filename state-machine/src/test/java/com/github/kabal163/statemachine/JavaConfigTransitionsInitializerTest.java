package com.github.kabal163.statemachine;

import com.github.kabal163.statemachine.api.LifecycleConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class JavaConfigTransitionsInitializerTest {

    JavaConfigTransitionsInitializer javaConfigTransitionsInitializer;

    @BeforeEach
    void setUp() {
        javaConfigTransitionsInitializer = new JavaConfigTransitionsInitializer();
    }

    @Test
    void givenConfigurationsIsNullWhenInitializeThenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> javaConfigTransitionsInitializer.initialize(null));
    }

    @Test
    void givenConfigurationsIsEmptyWhenInitializeThenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> javaConfigTransitionsInitializer.initialize(emptyList()));
    }

    @Test
    void givenConfigurationReturnsLifecycleNameWhenInitializeThenReturnsTransitionsGroupedByLifecycleName() {
        OrderLifecycleConfiguration config = new OrderLifecycleConfiguration();
        Map<String, Set<Transition>> transitions = javaConfigTransitionsInitializer.initialize(singletonList(config));

        assertTrue(transitions.containsKey(config.getLifecycleName()));
    }

    @Test
    void givenConfigurationReturnsNullLifecycleNameWhenInitializeThenReturnsTransitionsGroupedByConfigurationClassName() {
        OrderLifecycleConfiguration config = spy(new OrderLifecycleConfiguration());
        when(config.getLifecycleName()).thenReturn(null);
        Map<String, Set<Transition>> transitions = javaConfigTransitionsInitializer.initialize(singletonList(config));

        assertTrue(transitions.containsKey(config.getClass().getCanonicalName()));
    }

    @Test
    void givenConfigurationReturnsEmptyLifecycleNameWhenInitializeThenReturnsTransitionsGroupedByConfigurationClassName() {
        OrderLifecycleConfiguration config = spy(new OrderLifecycleConfiguration());
        when(config.getLifecycleName()).thenReturn(" ");
        Map<String, Set<Transition>> transitions = javaConfigTransitionsInitializer.initialize(singletonList(config));

        assertTrue(transitions.containsKey(config.getClass().getCanonicalName()));
    }

    static class OrderLifecycleConfiguration implements LifecycleConfiguration {

        @Override
        public void configureTransitions(TransitionConfigurer configurer) {}

        @Override
        public String getLifecycleName() {
            return "LIFECYCLE_NAME";
        }
    }
}