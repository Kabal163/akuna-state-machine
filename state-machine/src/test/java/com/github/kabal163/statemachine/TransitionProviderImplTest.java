package com.github.kabal163.statemachine;

import com.github.kabal163.statemachine.api.LifecycleConfiguration;
import com.github.kabal163.statemachine.api.TransitionsInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Set;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TransitionProviderImplTest {

    static final String LIFECYCLE_NAME = "lifecycleTestName";

    @Mock
    TransitionsInitializer transitionsInitializer;

    @Mock
    LifecycleConfiguration lifecycleConfiguration;

    @Mock
    Transition transition;

    TransitionProviderImpl transitionProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(transitionsInitializer.initialize(any()))
                .thenReturn(singletonMap(LIFECYCLE_NAME, Set.of(transition)));
        transitionProvider = TransitionProviderImpl.builder()
                .transitionInitializer(transitionsInitializer)
                .configs(singletonList(lifecycleConfiguration))
                .build();
    }
}