package ru.akuna.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akuna.entity.Event;
import ru.akuna.entity.Order;
import ru.akuna.entity.State;
import ru.akuna.repository.OrderRepository;
import ru.akuna.statemachine.api.LifecycleManager;
import ru.akuna.statemachine.api.TransitionResult;

import java.util.HashMap;
import java.util.Map;

import static ru.akuna.entity.Event.CANCEL;
import static ru.akuna.entity.Event.CREATE;
import static ru.akuna.entity.Event.DELIVER;
import static ru.akuna.entity.Event.PAY;
import static ru.akuna.lifecycle.Constants.CURRENCY;
import static ru.akuna.lifecycle.Constants.SUM;

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
        contextVariables.put(CURRENCY, currency);
        contextVariables.put(SUM, amount);

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
