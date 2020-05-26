package io.github.kabal163.samples.repository;

import io.github.kabal163.samples.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryOrderRepository implements OrderRepository {

    private final Map<String, Order> orders = new HashMap<>();

    @Override
    public Order save(Order order) {
        orders.put(order.getId(), order);

        return order;
    }

    @Override
    public Optional<Order> find(String id) {
        return Optional.of(orders.get(id));
    }

    @Override
    public Order delete(String id) {
        return orders.remove(id);
    }
}
