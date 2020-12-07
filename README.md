<h2>Akuna State Machine</h2>

The state machine is a mathematical model of computation. 
It is an abstract machine that can be in exactly one of a finite number of states at any given time.
This library helps you to implement the model.

<h3>Terminology</h3>

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

<h3>Add dependency in your project</h3>

    <dependency>
        <groupId>com.github.kabal163</groupId>
        <artifactId>state-machine</artifactId>
        <version>0.4.0</version>
    </dependency>

You need to implement <code>LifecycleConfiguration</code> and configure your lifecycle.

The main class you interact with is <code>LifecycleManager</code>. It responsible
for applying appropriate transition to your entity according entities current state
and incoming event.

You need to create it using its default implementation <code>LifecycleManagerImpl</code>.

    @Bean
    public LifecycleManager<State, Event> myLifecycleManager(LifecycleConfiguration<State, Event> configuration) {
        Map<String, Lifecycle<State, Event>> lifecyclesByName = new JavaConfigLifecyclesInitializer().initialize(singleton(configuration));
        TransitionProvider<State, Event> transitionProvider = new TransitionProviderImpl<>(lifecyclesByName);
        return new LifecycleManagerImpl<>(transitionProvider);
    }

So, in order to create the <code>LifecycleManager</code> you need to:
1. Implement <code>LifecycleConfiguration</code> and describe your lifecycle
2. Use <code>JavaConfigLifecyclesInitializer</code> in order to create <code>Lifecycle</code> from the <code>LifecycleConfiguration</code>
3. Create <code>TransitionProvider</code> and pass the created lifecycle to it.
4. Create the <code>LifecycleManager</code> and pass the <code>TransitionProvider</code> to it.

That's all.

In order to perform a transition you need to use the <code>execute</code> method of 
<code>LifecycleManager</code>. It receives the stateful object, event and optionally a map of any
variables which are needed during a transition. <code>LifecycleManager</code> will put those variables
to the state context. You can use the state context in your conditions and actions. Also, you can
share some data between conditions and actions via the state context. 

Example:

    lifecycleManager.execute(myStatefulObject, MY_EVENT)

This method returns <code>TransitionResult</code> which contains information about performed transition.