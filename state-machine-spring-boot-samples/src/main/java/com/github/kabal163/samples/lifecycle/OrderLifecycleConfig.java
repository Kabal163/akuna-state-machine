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
public class OrderLifecycleConfig implements LifecycleConfiguration {

    private final Action generateIDAction;
    private final Action setCreationTimestampAction;
    private final Action setCanceledTimestampAction;
    private final Action setPaidTimestampAction;
    private final Action setDeliveredTimestampAction;

    private final Condition currencyCondition;

    @Override
    public void configureTransitions(TransitionConfigurer configurer) {
        configurer
                .with()
                .sourceState(INIT.name())
                .targetState(NEW.name())
                .event(CREATE.name())
                .action(generateIDAction)
                .action(setCreationTimestampAction)

                .with()
                .sourceState(NEW.name())
                .targetState(PAID.name())
                .event(PAY.name())
                .condition(currencyCondition)
                .action(setPaidTimestampAction)

                .with()
                .sourceState(NEW.name())
                .targetState(CANCELED.name())
                .event(CANCEL.name())
                .action(setCanceledTimestampAction)

                .with()
                .sourceState(PAID.name())
                .targetState(DELIVERED.name())
                .event(DELIVER.name())
                .action(setDeliveredTimestampAction);
    }
}
