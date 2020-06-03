package com.github.kabal163.samples.entity;

import lombok.Data;
import com.github.kabal163.statemachine.api.StatefulObject;

import java.time.LocalDateTime;

import static com.github.kabal163.samples.entity.State.INIT;

@Data
public class Order implements StatefulObject<State> {

    private String id;

    private State state = INIT;

    private LocalDateTime createdTimestamp;

    private LocalDateTime paidTimestamp;

    private LocalDateTime deliveredTimestamp;

    private LocalDateTime canceledTimestamp;
}