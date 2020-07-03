package com.github.kabal163.samples.lifecycle.action;

import org.springframework.stereotype.Component;
import com.github.kabal163.samples.entity.Event;
import com.github.kabal163.samples.entity.Order;
import com.github.kabal163.samples.entity.State;
import com.github.kabal163.statemachine.api.Action;
import com.github.kabal163.statemachine.api.StateContext;

import java.time.LocalDateTime;

@Component
public class SetCreationTimestampAction implements Action {

    @Override
    public void execute(StateContext context) {
        Order order = context.getStatefulObject();

        order.setCreatedTimestamp(LocalDateTime.now());
    }
}
