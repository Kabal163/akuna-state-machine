package ru.akuna.lifecycle.condition;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.akuna.entity.Event;
import ru.akuna.entity.State;
import ru.akuna.statemachine.api.Condition;
import ru.akuna.statemachine.api.StateContext;

import static ru.akuna.lifecycle.Constants.CURRENCY;

@Component
@Profile("rus")
@Qualifier("currencyCondition")
public class RurCondition implements Condition<State, Event> {

    private static final String RUR_CURRENCY = "RUR";

    @Override
    public boolean evaluate(StateContext<State, Event> context) {
        String currency = context.getVariable(CURRENCY, String.class);

        return RUR_CURRENCY.equals(currency);
    }
}
