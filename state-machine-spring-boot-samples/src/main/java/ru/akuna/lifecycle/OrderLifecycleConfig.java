package ru.akuna.lifecycle;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import ru.akuna.entity.Event;
import ru.akuna.entity.State;
import ru.akuna.statemachine.TransitionConfigurer;
import ru.akuna.statemachine.api.Action;
import ru.akuna.statemachine.api.Condition;
import ru.akuna.statemachine.api.LifecycleConfiguration;

import static ru.akuna.entity.Event.CANCEL;
import static ru.akuna.entity.Event.CREATE;
import static ru.akuna.entity.Event.DELIVER;
import static ru.akuna.entity.Event.PAY;
import static ru.akuna.entity.State.CANCELED;
import static ru.akuna.entity.State.DELIVERED;
import static ru.akuna.entity.State.INIT;
import static ru.akuna.entity.State.NEW;
import static ru.akuna.entity.State.PAID;

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
