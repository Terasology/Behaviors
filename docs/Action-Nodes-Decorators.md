# Actions and Decorators

Actions and Decorators are what enables any entity to actually exhibit behavior - they are the 'final' nodes which make an entity do things, and as such will be, alongside with the actual behavior trees, the parts of the Behavior API most useful to someone wishing to give its entity behavior.

## Class Structure

Action and Decorator nodes are implemented by the `ActionNode` and `DecoratorNode` classes, respectively.
Both implement the common `BehaviorNode` interface.

An `ActionNode` is a leaf node and thus has no children

A `DecoratorNode` has strictly one child.

What is important to note is, `ActionNode` and `DecoratorNode` both use an associated `Action` to do their actual work.
For every class with a `@BehaviorAction` annotation, a corresponding ActionNode or DecoratorNode is created, using the specified `Action` class as the action which provides the functionality (and using the `name` specified in the annotation to identify the node in the prefab files).

This means that in order to create a new Action or Decorator, typically _you do not want to create a new Node, but create an `Action` instead._

> [!NOTE|label:Managing State on Actions and Decorators]
> 
> In order to save substantially on memory and some performance, every ActionNode and DecoratorNode is created only once for a given behavior tree - these nodes are shared by all Actors running the same tree.
> This means that **storing stateful information in the Action itself is a terrible idea!** 
> 
> In order to store and read state information, access the `Actor`'s `dataMap` instead, using the Action's `getId()` as an index.

## `Action` and the `@BehaviorAction` annotation

In order to create a new Action or Decorator for use in your behavior trees, you want to create a new class which implements the `Action` interface - extending `BaseAction` is the best way, as you can only focus on the functional parts of the action.

In order for your `Action` to be discovered and loaded by the Behavior system, you need to annotate the class with the `@BehaviorAction` annotation. The format is:

```java
@BehaviorAction(name = "<name-of-your-action>")
```

and if the `Action` is supposed to be used in a Decorator, you add the `isDecorator` parameter:

```java
@BehaviorAction(name = "<name-of-your-decorator", isDecorator = true)
```

### Actions

Actions are leaves - nodes with no children. These typically have an effect on the world or the state of the entity - such as the `move-to`, `set-target-work` or `play-sound` actions.

An Action typically only needs to care about a few things:

- `construct(Actor)`
  
   called when the node is first reached. Useful for setting initial state - don't forget to **[save all state at the actor!](#notyet)**

- `modify(Actor, BehaviorState)` 
  
  called each time the node ticks. If the action is somewhat continuous, most of the work will be here. The second argument is irrelevant to an Action.

- `destruct(Actor)`
  
  called on the last tick of the node - when state changes from `RUNNING` to something final.

- `setup()` 
  
  a less used method - called right after all the fields are injected when the Action is being created. Can be useful if you need to load something once, but remember - all stateful data needs to be stored at the actor!

### Decorators

Actions designed for use Decorators implement the same methods as Actions designed as Actions, both related to the `DecoratorNode`'s child':

- `modify(Actor, BehaviorState)` 
  
  you now care about the second argument, which represents the `BehaviorState` returned by the child node. A Decorator often wants to react to it and/or modify it before returning its own state.

- `prune(Actor)` 
  
  called before update, return value decides whether to run child or not. Useful for condition-type nodes.
