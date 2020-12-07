package com.github.kabal163.statemachine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.kabal163.statemachine.api.LifecycleManager;
import com.github.kabal163.statemachine.api.StateContext;
import com.github.kabal163.statemachine.api.StatefulObject;
import com.github.kabal163.statemachine.api.TransitionResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LifecycleManagerImpl<S, E> implements LifecycleManager<S, E> {

    private static final Logger log = LoggerFactory.getLogger(LifecycleManagerImpl.class);

    private final TransitionProvider<S, E> transitionProvider;

    public LifecycleManagerImpl(TransitionProvider<S, E> transitionProvider) {
        this.transitionProvider = transitionProvider;
    }

    @Override
    public TransitionResult<S, E> execute(StatefulObject<S> statefulObject, E event) {
        return execute(statefulObject, event, new HashMap<>());
    }

    @Override
    public TransitionResult<S, E> execute(StatefulObject<S> statefulObject, E event, Map<String, Object> variables) {
        Objects.requireNonNull(statefulObject, "StatefulObject must not be null!");
        Objects.requireNonNull(event, "event must not be null!");
        Objects.requireNonNull(variables, "Map of variables must not be null!");

        Transition<S, E> transition = transitionProvider.getTransition(statefulObject, event);
        StateContext<S, E> context = new StateContext<>(statefulObject, event, variables);
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

        return new TransitionResult<>(
                success,
                context,
                transition.getSourceState(),
                transition.getTargetState(),
                exception);
    }
}
