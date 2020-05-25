package ru.akuna.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.akuna.entity.Order;
import ru.akuna.statemachine.exception.TransitionNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.akuna.entity.State.CANCELED;
import static ru.akuna.entity.State.DELIVERED;
import static ru.akuna.entity.State.NEW;
import static ru.akuna.entity.State.PAID;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("rus")
public class OrderServiceImplTest {

    @Autowired
    OrderService orderService;

    @Test
    void createOrder() {
        Order order = orderService.create();

        assertNotNull(order);
        assertNotNull(order.getId());
        assertNotNull(order.getCreatedTimestamp());
        assertEquals(NEW, order.getState());
    }

    @Test
    void payInRurIsAvailable() {
        Order order = orderService.create();
        orderService.pay(order.getId(), "RUR", 200);

        assertEquals(PAID, order.getState());
        assertNotNull(order.getPaidTimestamp());
    }

    @Test
    void payInEurIsNotAvailable() {
        Order order = orderService.create();
        orderService.pay(order.getId(), "EUR", 200);

        assertEquals(NEW, order.getState());
        assertNull(order.getPaidTimestamp());
    }

    @Test
    void cancelOrder() {
        Order order = orderService.create();
        orderService.cancel(order.getId());

        assertEquals(CANCELED, order.getState());
        assertNotNull(order.getCreatedTimestamp());
    }

    @Test
    void deliverOrder() {
        Order order = orderService.create();
        orderService.pay(order.getId(), "RUR", 200);
        orderService.deliver(order.getId());

        assertEquals(DELIVERED, order.getState());
        assertNotNull(order.getDeliveredTimestamp());
    }

    @Test
    void orderInNewStateCannotBeDelivered() {
        Order order = orderService.create();

        assertThrows(TransitionNotFoundException.class, () -> orderService.deliver(order.getId()));
    }

    @Test
    void payedOrderCannotBeCanceled() {
        Order order = orderService.create();
        orderService.pay(order.getId(), "RUR", 200);

        assertThrows(TransitionNotFoundException.class, () -> orderService.cancel(order.getId()));
    }
}