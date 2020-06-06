package com.github.kabal163.statemachine.testimpl;

import com.github.kabal163.statemachine.api.StatefulObject;
import lombok.Data;

@Data
public class Entity implements StatefulObject<State> {

    private String id;
    private State state;
}
