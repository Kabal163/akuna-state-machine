package com.github.kabal163.samples.entity;

import lombok.Data;
import com.github.kabal163.statemachine.api.StatefulObject;

import java.time.LocalDateTime;

import static com.github.kabal163.samples.entity.State.INIT;

@Data
public class Order implements StatefulObject {

    private String id;

    private State state = INIT;

    private LocalDateTime createdTimestamp;

    private LocalDateTime paidTimestamp;

    private LocalDateTime deliveredTimestamp;

    private LocalDateTime canceledTimestamp;

    @Override
    public void setState(String state) {
        this.state = State.valueOf(state);
    }

    @Override
    public String getLifecycleName() {
        return "orderLifecycle";
    }

    public String getState() {
        return state.name();
    }
}
