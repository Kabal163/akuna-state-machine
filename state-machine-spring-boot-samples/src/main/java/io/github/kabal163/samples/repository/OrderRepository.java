package io.github.kabal163.samples.repository;

import io.github.kabal163.samples.entity.Order;

import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> find(String id);

    Order delete(String id);
}