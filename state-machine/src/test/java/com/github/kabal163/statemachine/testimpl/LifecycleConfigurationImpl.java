package com.github.kabal163.statemachine.testimpl;

import com.github.kabal163.statemachine.TransitionConfigurer;
import com.github.kabal163.statemachine.api.LifecycleConfiguration;

import static com.github.kabal163.statemachine.testimpl.Event.APPROVE;
import static com.github.kabal163.statemachine.testimpl.Event.CANCEL;
import static com.github.kabal163.statemachine.testimpl.State.APPROVED;
import static com.github.kabal163.statemachine.testimpl.State.CANCELED;
import static com.github.kabal163.statemachine.testimpl.State.NEW;

public class LifecycleConfigurationImpl implements LifecycleConfiguration<State, Event> {

    @Override
    public void configureTransitions(TransitionConfigurer<State, Event> configurer) {
        configurer.with()
                .sourceState(NEW)
                .targetState(APPROVED)
                .event(APPROVE)

                .with()
                .sourceState(NEW)
                .targetState(CANCELED)
                .event(CANCEL);
    }
}
