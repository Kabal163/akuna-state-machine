package ru.akuna.entity;

import lombok.Data;
import ru.akuna.statemachine.api.StatefulObject;

import java.time.LocalDateTime;

import static ru.akuna.entity.State.INIT;

@Data
public class Order implements StatefulObject<State> {

    private String id;

    private State state = INIT;

    private LocalDateTime createdTimestamp;

    private LocalDateTime paidTimestamp;

    private LocalDateTime deliveredTimestamp;

    private LocalDateTime canceledTimestamp;
}
