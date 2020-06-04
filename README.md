<h2>Akuna State Machine</h2>

The state machine is a mathematical model of computation. 
It is an abstract machine that can be in exactly one of a finite number of states at any given time.
This library helps you to implement the model.

<h4>Terminology</h4>

<i>Stateful object</i> - any entity which implements <code>StatefulObject</code> interface and
has finite numbers of states.

<i>Source state</i> - the state of an entity in the moment of starting transition process

<i>Target state</i> - the desired state of an entity after finish of transition

<i>Event</i> - any signal notifying about some event

<i>Transition</i> - changing the entity's state from source to target according the incoming event

<i>State context</i> - a context which contains all necessary information for the transition

<h4>If you are using the library without the spring boot starter</h4>

    <dependency>
        <groupId>com.github.kabal163</groupId>
        <artifactId>state-machine</artifactId>
        <version>0.1</version>
    </dependency>

You need to implement <code>LifecycleConfiguration</code> and configure your lifecycle.
You can see how to do it in the "samples" module.

The main class you interact with is <code>LifecycleManager</code>. It responsible
for applying appropriate transition to your entity according entities current state
and incoming event.

You need to create using its default implementation <code>LifecycleManagerImpl</code>.
It requires <code>TransitionBuilder</code>. You can use the default implementation 
<code>TransitionBuilderImpl</code>.

Example:

<p>
   <code>LifecycleConfiguration config = new MyLifecycleConfiguration();</code><br>
   <code>TransitionBuilder transitionBuilder = new TransitionBuilderImpl();</code><br>
   <code>LifecycleManager lifecycleManager = new LifecycleManagerImpl(transitionBuilder, config);</code>
</p>

In order to perform a transition you need to use the <code>execute</code> method of 
<code>LifecycleManager</code>. It receives stateful object, event and optionally map of any
variables which are needed during a transition. <code>LifecycleManager</code> will put those variables
to the state context.
