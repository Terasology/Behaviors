# Big Picture

Here, you can find an overview of some of the inner workings of the Behavior system.

In Terasology, the Behavior system is composed of:

- A Component `Behavior` used for associating entities with behaviors;
- Two components `GroupTag` and `GroupMind` associated with groups;
- An implementation of behavior trees and their related mechanisms (used to describe the behavior of an entity or a group of entities);
- A decorated entity called `Actor`, used to facilitate the process of adding behaviors to a given entity;
- An asset mechanism for loading `.behavior` and `.group` from the assets structure (on disk); and
- An in-game editor for behavior trees (_currently unstable_).

More details on each of the parts can be found below.

## Components

The `Behavior` component is used to associate an existing behavior with an entity or creature.
Behaviors are structured as [behavior trees](Behavior-Trees) and are loaded as assets from [behavior files](Building-a-Behavior-Tree) (`.behavior`).
This component holds two different objects:

- A `BehaviorTree`, deserialized from a behavior file; and
- An `Interpreter`, responsible for evaluating the behavior tree.

The `GroupTag` component is used to assign an entity or creature to a group.
Also, the `GroupMind` component is used in conjunction with `GroupTag` in specific group-related scenarios, where all entities within a group must possess not only the same behavior tree but also the same behavior states (acting in unison).
For more details on how the group mechanism works please see [Groups](Groups).

## Behavior Trees

Behavior trees are the structures used to describe the behavior of an entity within the game.
A behavior is a pre-defined set of actions performed by an entity, triggered by specific conditions or events.
Behaviors can be related to movement (animals wandering in an open field) or more complex actions (searching for water sources to fulfill a specific need).
A graphical illustration of a typical behavior tree is shown below:

<fig src="images/big-picture/bt.png" alt="Example of a Behavior Tree">An example of a behavior tree.</fig>

Behavior trees are composed of different _nodes_, which determine if the behavior will be performed sequentially, or if it will be subject to any conditions.
The illustration above has _sequential_ nodes (right arrows), determining that everything below them will happen in a sequence (from left to right), and _condition_ nodes (question marks), determining that everything below them will happen (in sequence) only if a determined condition is satisfied.

In Terasology, nodes are objects implementing the `TreeNode` interface.
There are composite nodes, action nodes, and decorators, but in an effort not to duplicate information too much, see [Tree Nodes](Tree-Nodes) to learn about the `TreeNode` interface in detail.

Every `Actor` instance (see description below) has an associated `Interpreter`.
That interpreter uses a `BehaviorTreeRunner` (currently `DefaultBehaviorTreeRunner` as we aren't using the bytecode/ASM parts of the system) to work on the given tree - there is a `BehaviorTreeRunner` for every given Actor.
What's important is the `BehaviorTree` is a data class; it provides the underlying tree data, but it's shared between Actors, using the Interpreter/BehaviorTreeRunner combo.

Both `BehaviorTree` and `Interpreter` can also be instantiated and assigned to an entity during gameplay (for example, when entities join a group and must adopt a different behavior).
In this case, the `BehaviorTree` instance is replaced by a new one, while the `Interpreter` can be created as a new instance or it can be replaced by another existing instance (this is particularly important in cases where an entity must not only assume the new behavior but also have its state placed in a specific point within the behavior tree).

## Actor

The Actor is a decorated Entity, a class that facilitates adds Behavior related functionality to a given Entity.
It represents an entity with a behavior.

Important parts of the Actor class:

- `Actor(EntityRef)` constructor - an Actor can only be constructed over a given Entity.
- `getEntity()` - returns the underlying entity.
- `getComponent()`, `getComponentField()`, `hasComponent()` - QoL methods providing easier access to some parts of the underlying entity.
- `save(Component component)` - assigns a component to the current Actor. This method is used when new components are assigned to the Actor, or in some cases when existing components are modified.

Other important parts of the Actor class are:

- `Map<Integer, Object> dataMap`
  
  _Used by every Behavior Node to manipulate its stateful information, so the Nodes themselves can be stateless/reusable.
  The `id` arguments used in its related `getValue()` and `setValue()` methods are the tree-unique IDs of the Nodes._

- `Map<String, Object> blackboard`
  
  _Used to facilitate inter-node communication.
  While in `dataMap` every node has its own little corner where it stores its info, `blackboard` is the shared space where nodes can co-ordinate any higher level stateful goals._

## Assets

Behaviors are JSON-like files that describe a behavior tree, containing pre-defined elements (nodes) and actions associated with these nodes.
These files (`.behavior`) are located in the `assets/behaviors` folder of each module.

Each entity can have a behavior component included in its `.prefab` file as:

```json
"Behavior" : {
    "tree" : "<module>:<behavior>"
}
```

Where `<behavior>` refers to the name of the behavior, and `<module>` refers to the module in which the behavior is defined. You can use any pre-made behavior existing in another module. If you use the `stray` behavior from the Behaviors module, for example, your `.prefab` behavior entry will be:

```json
"Behavior" : {
    "tree" : "Behaviors:stray"
}
```

> [!NOTE]
> Although you can omit the `<module>` prefix for behaviors defined in the same module, it is good practice to **always** use the fully qualified name.
> 
> This makes it easier to extract and move behaviors to different modules, and it also avoids confusion between behaviors of similar or identical names from different modules.

A curated list of pre-made behaviors can be found [here](Pre-made-Behaviors-and-Nodes).

## GUI Editor

Behavior trees can also be created or edited through the use of the Behavior GUI editor.
The GUI editor is currently unstable, but its latest version can be accessed in-game through the <kbd>F5</kbd> key.

> [!NOTE]
> The UI editor is in a highly experimental state.
> We recommend that you edit your `.behavior` files directly in a text editor of your choice.
