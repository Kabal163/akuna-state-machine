package com.github.kabal163.samples.lifecycle.condition;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import com.github.kabal163.samples.entity.Event;
import com.github.kabal163.samples.entity.State;
import com.github.kabal163.statemachine.api.Condition;
import com.github.kabal163.statemachine.api.StateContext;

import static com.github.kabal163.samples.lifecycle.Constants.CURRENCY;

@Component
@Profile("eur")
@Qualifier("currencyCondition")
public class EuroCondition implements Condition<State, Event> {

    private static final String EURO_CURRENCY = "EUR";

    @Override
    public boolean evaluate(StateContext<State, Event> context) {
        String currency = context.getVariable(CURRENCY, String.class);

        return EURO_CURRENCY.equals(currency);
    }
}
