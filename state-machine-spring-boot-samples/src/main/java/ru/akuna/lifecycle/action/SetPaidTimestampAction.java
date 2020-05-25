package ru.akuna.lifecycle.action;

import org.springframework.stereotype.Component;
import ru.akuna.entity.Event;
import ru.akuna.entity.Order;
import ru.akuna.entity.State;
import ru.akuna.statemachine.api.Action;
import ru.akuna.statemachine.api.StateContext;

import java.time.LocalDateTime;

@Component
public class SetPaidTimestampAction implements Action<State, Event> {

    @Override
    public void execute(StateContext<State, Event> context) {
        Order order = context.getStatefulObject();

        order.setPaidTimestamp(LocalDateTime.now());
    }
}
