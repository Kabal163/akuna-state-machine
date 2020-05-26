package io.github.kabal163.samples.service;

import io.github.kabal163.samples.lifecycle.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import io.github.kabal163.samples.entity.Event;
import io.github.kabal163.samples.entity.Order;
import io.github.kabal163.samples.entity.State;
import io.github.kabal163.samples.repository.OrderRepository;
import io.github.kabal163.statemachine.api.LifecycleManager;
import io.github.kabal163.statemachine.api.TransitionResult;

import java.util.HashMap;
import java.util.Map;

import static io.github.kabal163.samples.entity.Event.CANCEL;
import static io.github.kabal163.samples.entity.Event.CREATE;
import static io.github.kabal163.samples.entity.Event.DELIVER;
import static io.github.kabal163.samples.entity.Event.PAY;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final LifecycleManager<State, Event> lifecycleManager;
    private final OrderRepository repository;

    @Override
    public Order create() {
        TransitionResult<State, Event> result = lifecycleManager.execute(new Order(), CREATE);
        Order order = result
                .getStateContext()
                .getStatefulObject();

        return repository.save(order);
    }

    @Override
    public Order pay(String id, String currency, double amount) {
        Order order = repository.find(id).orElseThrow();
        Map<String, Object> contextVariables = new HashMap<>();
        contextVariables.put(Constants.CURRENCY, currency);
        contextVariables.put(Constants.SUM, amount);

        lifecycleManager.execute(order, PAY, contextVariables);

        return repository.save(order);
    }

    @Override
    public Order cancel(String id) {
        Order order = repository.find(id).orElseThrow();
        lifecycleManager.execute(order, CANCEL);

        return repository.save(order);
    }

    @Override
    public Order deliver(String id) {
        Order order = repository.find(id).orElseThrow();
        lifecycleManager.execute(order, DELIVER);

        return repository.save(order);
    }
}
