package com.github.kabal163.statemachine;

import com.github.kabal163.statemachine.api.LifecycleConfiguration;
import com.github.kabal163.statemachine.testimpl.Event;
import com.github.kabal163.statemachine.testimpl.LifecycleConfigurationImpl;
import com.github.kabal163.statemachine.testimpl.State;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class LifecycleManagerImplTest {

    LifecycleManagerImpl<State, Event> lifecycleManager;
    LifecycleConfiguration<State, Event> lifecycleConfiguration;

    LifecycleManagerImplTest() {
        lifecycleConfiguration = new LifecycleConfigurationImpl();
    }

    @BeforeEach
    void setUp() {
        lifecycleManager = new LifecycleManagerImpl<>(new TransitionBuilderImpl<>(), lifecycleConfiguration);
    }


}