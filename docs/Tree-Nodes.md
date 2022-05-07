# Tree Nodes

Every Behavior Tree is composed of many Nodes.
Here we shine some light on what the Nodes actually are.

## The Behavior Node Interface

Every Node implements the `BehaviorNode` interface.
Check the [Javadoc for BehaviorNode](https://jenkins.terasology.io/teraorg/job/Terasology/job/engine/job/develop/javadoc/org/terasology/engine/logic/behavior/core/BehaviorNode.html) for the full API specification.

The important bits are:

- `getName()`
  
  required for every node - this is what identifies the node for the BT / prefab system.

- `construct(actor)`
  
  called when a Node begins executing - when it is reached in the tree.

- `execute(actor)`
  
  called each time the node is executed - when the interpreter ticks and the tree is in a state where the node is executing.  

- `destruct(actor)`
  
  called when a node finishes executing.

- child management methods
  
  These are self-explanatory, with children stored by index.
  The implementation differs widely based on node type - more info can be found in their respective categories.

## Node Types

### Control Flow Nodes

Control Flow nodes are responsible for the _logic_ parts of the behavior tree - they ensure some nodes are only run in a sequence, some run in parallel, some are run exactly _n_ amount of times, etc.

Some `Decorator` nodes fall into this category (e.g. Timer, Inverter, Counter).

More on control flow nodes here: [Control Flow Nodes](Control-Flow-Nodes).  


## Action Nodes and Decorators

Action and Decorator nodes are what most creators will be concerned with, in terms of creating new nodes.

- Action nodes are leaves - nodes with no children. They are what enables the entity to have visible behavior traits - the actual actions the entity takes are defined in `Actions`.

- Decorator nodes are nodes with strictly one child, usually an `ActionNode`, adding functionality to the child or modifying its return state.

Actions and Decorators are loaded dynamically using the `@BehaviorAction` annotation.

An in-depth look at Actions, Decorators and how to use them is here: [Action Nodes, Decorators](Action-Nodes-Decorators).  

> [!NOTE]
> In order to save on memory and some processor cycles, some nodes (notably Actions and Decorators) are created only once per behavior tree, and are stateless in nature - the state *needs to be stored at the Actor*. 
> More on this [here](Action-Nodes-Decorators#important-note-on-state---if-youre-creating-your-own-actions-read).  
