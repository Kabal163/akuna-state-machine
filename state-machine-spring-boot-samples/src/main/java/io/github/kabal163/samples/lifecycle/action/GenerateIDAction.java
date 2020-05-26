package io.github.kabal163.samples.lifecycle.action;

import org.springframework.stereotype.Component;
import io.github.kabal163.samples.entity.Event;
import io.github.kabal163.samples.entity.Order;
import io.github.kabal163.samples.entity.State;
import io.github.kabal163.statemachine.api.Action;
import io.github.kabal163.statemachine.api.StateContext;

import java.util.UUID;

@Component
public class GenerateIDAction implements Action<State, Event> {

    @Override
    public void execute(StateContext<State, Event> context) {
        Order order = context.getStatefulObject();

        order.setId(UUID.randomUUID().toString());
    }
}
