package com.github.kabal163.statemachine;

import java.util.Set;

interface TransitionBuilder extends TransitionConfigurer {

    Set<Transition> buildTransitions();
}
