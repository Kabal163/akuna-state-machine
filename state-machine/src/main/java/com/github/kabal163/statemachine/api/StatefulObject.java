package com.github.kabal163.statemachine.api;

/**
 * Any entity which is needed to be managed by {@link LifecycleManager}
 * lifecycle manager must implement this interface. This provides
 * the minimum of methods which are necessary for executing a transition.
 */
public interface StatefulObject {

    /**
     * Returns the stateful object's id
     *
     * @param <T> type of id
     * @return a stateful object's id
     */
    <T> T getId();

    /**
     * Returns the actual state of the stateful object
     *
     * @return the actual state of the stateful object
     */
    String getState();

    /**
     * Sets the state to the stateful object. Should not be
     * used outside of the {@link LifecycleManager} lifecycle manager
     *
     * @param state new state to the stateful object
     */
    void setState(String state);

    /**
     * Must return the name of the appropriate lifecycle which transitions
     * should be applied to the current stateful object.
     * The name should be as specified in the {@link LifecycleConfiguration#getLifecycleName()}
     *
     * @return lifecycle's name
     */
    String getLifecycleName();
}
