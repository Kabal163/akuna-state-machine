package com.github.kabal163.statemachine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.github.kabal163.statemachine.api.LifecycleConfiguration;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class JavaConfigLifecyclesInitializerTest {

    JavaConfigLifecyclesInitializer javaConfigTransitionsInitializer;

    @BeforeEach
    void setUp() {
        javaConfigTransitionsInitializer = new JavaConfigLifecyclesInitializer();
    }

    @Test
    @DisplayName("Given configurations is null " +
            "When call JavaConfigLifecyclesInitializer.initialize " +
            "Then throws NullPointerException")
    void givenConfigurationsIsNull_whenInitialize_thenThrowsNullPointerException() {
        assertThatThrownBy(() -> javaConfigTransitionsInitializer.initialize(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("must not be null");
    }

    @Test
    @DisplayName("Given configurations is empty collection " +
            "When call JavaConfigLifecyclesInitializer.initialize " +
            "Then throws IllegalArgumentException")
    void givenConfigurationsIsEmpty_whenInitialize_thenThrowsIllegalArgumentException() {
        List<LifecycleConfiguration<TestState, TestEvent>> emptyList = emptyList();
        assertThatThrownBy(() -> javaConfigTransitionsInitializer.initialize(emptyList))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be empty");
    }

    @Test
    @DisplayName("When call JavaConfigLifecyclesInitializer.initialize " +
            "Then returns not null")
    void whenInitialize_thenReturnsNotNull() {
        TestLifecycleConfiguration config = new TestLifecycleConfiguration();
        Map<String, Lifecycle<TestState, TestEvent>> actual = javaConfigTransitionsInitializer.initialize(singletonList(config));

        assertThat(actual).isNotNull();
    }

    @Test
    @DisplayName("Given TestLifecycleConfiguration.getLifecycleName returns null " +
            "When call JavaConfigLifecyclesInitializer.initialize " +
            "Then returns lifecycle with name of it's implementation class")
    void givenConfiguration$getLifecycleNameReturnsNull_whenInitialize_thenReturnsLifecycleWithNameOfImplementationClass() {
        TestLifecycleConfiguration config = spy(new TestLifecycleConfiguration());
        when(config.getLifecycleName()).thenReturn(null);
        Map<String, Lifecycle<TestState, TestEvent>> lifecyclesByName = javaConfigTransitionsInitializer.initialize(singletonList(config));
        Lifecycle<TestState, TestEvent> actual = lifecyclesByName.get(config.getClass().getCanonicalName());

        assertThat(actual.getName()).isEqualTo(config.getClass().getCanonicalName());
    }

    @Test
    @DisplayName("Given TestLifecycleConfiguration.getLifecycleName returns empty string " +
            "When call JavaConfigLifecyclesInitializer.initialize " +
            "Then returns lifecycle with name of it's implementation class")
    void givenConfiguration$getLifecycleNameReturnsEmptyString_whenInitialize_thenReturnsLifecycleWithNameOfImplementationClass() {
        TestLifecycleConfiguration config = spy(new TestLifecycleConfiguration());
        when(config.getLifecycleName()).thenReturn(EMPTY);
        Map<String, Lifecycle<TestState, TestEvent>> lifecyclesByName = javaConfigTransitionsInitializer.initialize(singletonList(config));
        Lifecycle<TestState, TestEvent> actual = lifecyclesByName.get(config.getClass().getCanonicalName());

        assertThat(actual.getName()).isEqualTo(config.getClass().getCanonicalName());
    }

    static class TestLifecycleConfiguration implements LifecycleConfiguration<TestState, TestEvent> {

        @Override
        public void configureTransitions(TransitionConfigurer<TestState, TestEvent> configurer) {}

        @Override
        public String getLifecycleName() {
            return "LIFECYCLE_NAME";
        }
    }
}