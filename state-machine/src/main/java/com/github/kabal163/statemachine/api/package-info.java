/**
 * Provides API to work with state machine.
 *
 * @see com.github.kabal163.statemachine.api.LifecycleConfiguration - describes
 * transitions of a {@link com.github.kabal163.statemachine.api.StatefulObject stateful object}
 * Implement this interface and describe your configuration.
 *
 * @see com.github.kabal163.statemachine.api.Action - implement interface and use
 * your implementations in the lifecycle configuration.
 *
 * @see com.github.kabal163.statemachine.api.Condition - implement interface and use
 * your implementations in the lifecycle configuration.
 *
 * @see com.github.kabal163.statemachine.api.LifecycleManager - the entry point to interract with the
 * state machine. Provides ability to change the state of the stateful object according the specified
 * lifecycle configuration.
 */
@ParametersAreNonnullByDefault
package com.github.kabal163.statemachine.api;

import javax.annotation.ParametersAreNonnullByDefault;