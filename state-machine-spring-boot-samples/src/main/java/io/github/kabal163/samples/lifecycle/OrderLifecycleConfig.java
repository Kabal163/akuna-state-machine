package io.github.kabal163.samples.lifecycle;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import io.github.kabal163.samples.entity.Event;
import io.github.kabal163.samples.entity.State;
import io.github.kabal163.statemachine.TransitionConfigurer;
import io.github.kabal163.statemachine.api.Action;
import io.github.kabal163.statemachine.api.Condition;
import io.github.kabal163.statemachine.api.LifecycleConfiguration;

import static io.github.kabal163.samples.entity.Event.CANCEL;
import static io.github.kabal163.samples.entity.Event.CREATE;
import static io.github.kabal163.samples.entity.Event.DELIVER;
import static io.github.kabal163.samples.entity.Event.PAY;
import static io.github.kabal163.samples.entity.State.CANCELED;
import static io.github.kabal163.samples.entity.State.DELIVERED;
import static io.github.kabal163.samples.entity.State.INIT;
import static io.github.kabal163.samples.entity.State.NEW;
import static io.github.kabal163.samples.entity.State.PAID;

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
