<h2>Akuna State Machine</h2>

The state machine is a mathematical model of computation. 
It is an abstract machine that can be in exactly one of a finite number of states at any given time.
This library helps you to implement the model.

<h4>Terminology</h4>

<b><i>Stateful object</i></b> - any entity which implements <code>StatefulObject</code> interface and
has finite numbers of states.

<b><i>Source state</i></b> - the state of an entity in the moment of starting transition process.

<b><i>Target state</i></b> - the desired state of an entity after finish of transition.

<b><i>Event</i></b> - any signal notifying about some event.

<b><i>Transition</i></b> - changing the entity's state from source to target according the incoming event.

<b><i>State context</i></b> - a context which contains all necessary information for the transition.

<b><i>Action</i></b> - the step of transition which performs some logic. If any action fails then
the whole transition will be treated as failed. 

<b><i>Condition</i></b> - a rule which determines possibility of transition at this moment. If any condition
returns false then transition will not be performed.

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

    LifecycleConfiguration config = new MyLifecycleConfiguration();
    TransitionBuilder transitionBuilder = new TransitionBuilderImpl();
    LifecycleManager lifecycleManager = new LifecycleManagerImpl(transitionBuilder, config);


In order to perform a transition you need to use the <code>execute</code> method of 
<code>LifecycleManager</code>. It receives stateful object, event and optionally a map of any
variables which are needed during a transition. <code>LifecycleManager</code> will put those variables
to the state context. You can use the state context in your conditions and actions. Also, you can
share some data between conditions and actions via the state context. 

Example:

<p>
    <code>lifecycleManager.execute(myStatefulObject, MY_EVENT)</code>
</p>

This method returns <code>TransitionResult</code> which contains information about performed transition.

<h4>If you are using the state-machine-spring-boot-starter</h4>

    <dependency>
        <groupId>com.github.kabal163</groupId>
        <artifactId>state-machine-spring-boot-starter</artifactId>
        <version>0.1</version>
    </dependency>

All you need to do is to implement <code>LifecycleConfiguration</code> and define it as a bean.
That's all. Now you can inject <code>LifecycleManager</code> in your service and voila. Now enjoy it.