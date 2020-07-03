package com.github.kabal163.samples.service;

import com.github.kabal163.samples.lifecycle.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.github.kabal163.samples.entity.Event;
import com.github.kabal163.samples.entity.Order;
import com.github.kabal163.samples.entity.State;
import com.github.kabal163.samples.repository.OrderRepository;
import com.github.kabal163.statemachine.api.LifecycleManager;
import com.github.kabal163.statemachine.api.TransitionResult;

import java.util.HashMap;
import java.util.Map;

import static com.github.kabal163.samples.entity.Event.CANCEL;
import static com.github.kabal163.samples.entity.Event.CREATE;
import static com.github.kabal163.samples.entity.Event.DELIVER;
import static com.github.kabal163.samples.entity.Event.PAY;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final LifecycleManager lifecycleManager;
    private final OrderRepository repository;

    @Override
    public Order create() {
        TransitionResult result = lifecycleManager.execute(new Order(), CREATE.name());
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

        lifecycleManager.execute(order, PAY.name(), contextVariables);

        return repository.save(order);
    }

    @Override
    public Order cancel(String id) {
        Order order = repository.find(id).orElseThrow();
        lifecycleManager.execute(order, CANCEL.name());

        return repository.save(order);
    }

    @Override
    public Order deliver(String id) {
        Order order = repository.find(id).orElseThrow();
        lifecycleManager.execute(order, DELIVER.name());

        return repository.save(order);
    }
}
