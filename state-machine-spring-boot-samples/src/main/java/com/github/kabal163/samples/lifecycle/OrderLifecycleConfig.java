package com.github.kabal163.samples.lifecycle;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import com.github.kabal163.samples.entity.Event;
import com.github.kabal163.samples.entity.State;
import com.github.kabal163.statemachine.TransitionConfigurer;
import com.github.kabal163.statemachine.api.Action;
import com.github.kabal163.statemachine.api.Condition;
import com.github.kabal163.statemachine.api.LifecycleConfiguration;

import static com.github.kabal163.samples.entity.Event.CANCEL;
import static com.github.kabal163.samples.entity.Event.CREATE;
import static com.github.kabal163.samples.entity.Event.DELIVER;
import static com.github.kabal163.samples.entity.Event.PAY;
import static com.github.kabal163.samples.entity.State.CANCELED;
import static com.github.kabal163.samples.entity.State.DELIVERED;
import static com.github.kabal163.samples.entity.State.INIT;
import static com.github.kabal163.samples.entity.State.NEW;
import static com.github.kabal163.samples.entity.State.PAID;

@Configuration
@RequiredArgsConstructor
public class OrderLifecycleConfig implements LifecycleConfiguration<State, Event> {

    private final Action<State, Event> generateIDAction;
    private final Action<State, Event> setCreationTimestampAction;
    private final Action<State, Event> setCanceledTimestampAction;
    private final Action<State, Event> setPaidTimestampAction;
    private final Action<State, Event> setDeliveredTimestampAction;

    private final Condition<State, Event> currencyCondition;

    @Override
    public void configureTransitions(TransitionConfigurer<State, Event> configurer) {
        configurer
                .with()
                .sourceState(INIT)
                .targetState(NEW)
                .event(CREATE)
                .action(generateIDAction)
                .action(setCreationTimestampAction)

                .with()
                .sourceState(NEW)
                .targetState(PAID)
                .event(PAY)
                .condition(currencyCondition)
                .action(setPaidTimestampAction)

                .with()
                .sourceState(NEW)
                .targetState(CANCELED)
                .event(CANCEL)
                .action(setCanceledTimestampAction)

                .with()
                .sourceState(PAID)
                .targetState(DELIVERED)
                .event(DELIVER)
                .action(setDeliveredTimestampAction);
    }
}
