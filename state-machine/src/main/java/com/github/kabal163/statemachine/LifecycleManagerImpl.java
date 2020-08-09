package com.github.kabal163.statemachine;

import com.github.kabal163.statemachine.api.LifecycleManager;
import com.github.kabal163.statemachine.api.StateContext;
import com.github.kabal163.statemachine.api.StatefulObject;
import com.github.kabal163.statemachine.api.TransitionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class LifecycleManagerImpl implements LifecycleManager {

    private final TransitionProvider transitionProvider;

    @Override
    public TransitionResult execute(StatefulObject statefulObject, String event) {
        return execute(statefulObject, event, new HashMap<>());
    }

    @Override
    public TransitionResult execute(StatefulObject statefulObject, String event, Map<String, Object> variables) {
        Transition transition = transitionProvider.getTransition(statefulObject, event);
        StateContext context = new StateContext(statefulObject, event, variables);
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

        return new TransitionResult(
                success,
                context,
                transition.getSourceState(),
                transition.getTargetState(),
                exception);
    }
}
