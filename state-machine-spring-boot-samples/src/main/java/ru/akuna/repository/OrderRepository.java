package ru.akuna.repository;

import ru.akuna.entity.Order;

import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> find(String id);

    Order delete(String id);
}
