package ru.akuna.lifecycle.action;

import org.springframework.stereotype.Component;
import ru.akuna.entity.Event;
import ru.akuna.entity.Order;
import ru.akuna.entity.State;
import ru.akuna.statemachine.api.Action;
import ru.akuna.statemachine.api.StateContext;

import java.util.UUID;

@Component
public class GenerateIDAction implements Action<State, Event> {

    @Override
    public void execute(StateContext<State, Event> context) {
        Order order = context.getStatefulObject();

        order.setId(UUID.randomUUID().toString());
    }
}
