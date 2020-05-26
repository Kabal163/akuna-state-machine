package io.github.kabal163.samples.service;

import io.github.kabal163.samples.entity.Order;

/**
 * The service provides basic operations over {@link Order}
 * to demonstrate how to work with Akuna State Machine
 */
public interface OrderService {

    /**
     * Creates an order in new state
     *
     * @return new order
     */
    Order create();

    /**
     * Transits an order to the paid state
     * Currency must meet the active profile otherwise the order will not be transited
     * to the paid state
     *
     * @param id is identifier of an {@link Order}
     * @param currency is a payment currency
     * @param amount is amount of paid money in appropriate currency
     * @return an order in paid state if currency meets profile constraints, otherwise
     * return the order with old state
     */
    Order pay(String id, String currency, double amount);

    /**
     * Transits an order to the canceled state
     *
     * @param id is identifier of an {@link Order}
     * @return canceled order
     */
    Order cancel(String id);

    /**
     * Transits an order to the delivered state
     *
     * @param id is identifier of an {@link Order}
     * @return delivered order
     */
    Order deliver(String id);
}
