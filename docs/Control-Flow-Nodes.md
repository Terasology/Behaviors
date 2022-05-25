# Control Flow Nodes

The control flow (logic) nodes are the building blocks of any behavior tree.
They provide the tools necessary to do many kinds of logic - run a node `n` times, run a given node only if the node before it succeeded, run nodes in parallel, run a node only for a given amount of time, etc.

> [!NOTE]
> The term used to reference a node in behavior definitions is usually derived from the node name, for instance
>
> primary name: **DynamicSelector**
> technical name: `dynamic` (used in the behavior files)

Here's a reference of the logic nodes with their respective functions:

- [Control Flow Nodes](#control-flow-nodes)
  - [Special nodes](#special-nodes)
    - [Lookup `lookup`](#lookup-lookup)
    - [Log `log`](#log-log)
  - [Composite nodes](#composite-nodes)
    - [Sequence `sequence`](#sequence-sequence)
    - [Selector `selector`](#selector-selector)
    - [DynamicSelector `dynamic`](#dynamicselector-dynamic)
    - [Parallel `parallel`](#parallel-parallel)
  - [Decorator nodes](#decorator-nodes)
    - [Counter `counter`](#counter-counter)
    - [Invert `invert`](#invert-invert)
    - [Loop `loop`](#loop-loop)
    - [Timeout `timeout`](#timeout-timeout)

## Special nodes

### Lookup `lookup`

'Looks up' and runs a behavior tree.

#### Parameters:

- `tree` : the requested tree, in the form `'<module>:treeName'`

`SUCCESS`: when the tree finishes with `SUCCESS`.

`RUNNING`: if the tree is still `RUNNING`

`FAILURE`: when the tree finishes with `FAILURE`.

### Log `log`

Logs a message

Example:

```js
sequence: [
  {
    log: {
      message: "walk to target"
    }
  },
  //...
]
```

## Composite nodes

Composite nodes are nodes with more than one child - essential in various aspects of tree building.

If you want to perform a series of actions that each depend on the previous ones succeeding - like a chain - a [Sequence](#sequence-sequence) will be useful.

If you want to have conditional logic that selects between different behavior branches based on some conditions, a [DynamicSelector](#dynamicselector-dynamic) is recommended.

If you want to run different branches in parallel, use the [Parallel](#parallel-parallel) node.

Each of these nodes takes their children in arguments in this form:

```yaml
{ <name-of-composite-node>: [<child-1>, <child-2>, <...>, <child-n>] }
```

### Sequence `sequence`

Evaluates the children one by one, left to right.
Once a child finishes with `SUCCESS`, starts the next child.

`SUCCESS`: when all children finished with `SUCCESS`.

`FAILURE`: as soon as a child finishes with `FAILURE`.

### Selector `selector`

Evaluates the children one by one, until a child returns `SUCCESS`.
When a child finishes with `FAILURE`, evaluates the next in line.
If a child is `RUNNING`, doesn't re-evaluate previous children for state changes - until the Selector returns and is run again from the start.
If you want the dynamic re-evaluation, use [DynamicSelector](#dynamicselector-dynamic).

`SUCCESS`: as soon as a child finishes with `SUCCESS`.

`FAILURE`: when all children finished with `FAILURE`.

### DynamicSelector `dynamic`

Works like a Selector, but with each tick re-evaluates all children that have previously returned `FAILURE` or are currently `RUNNING`, making it useful for branching logic, when the top-level children are condition nodes of some description.

More on this here: [Responding to outside changes](Responding-to-Outside-Changes)

`SUCCESS`: as soon as a child finishes with `SUCCESS`.

`FAILURE`: when all children finished with `FAILURE`.

### Parallel `parallel`

All children are evaluated in parallel.

`SUCCESS`: when all children finished with `SUCCESS`.

`FAILURE`: as soon as a child finishes with `FAILURE`.

## Decorator nodes

Decorator nodes have exactly one child - useful to add functionality to existing nodes, modify what a node returns, etc.

Every Decorator takes its child as a `child: ` argument, e.g.

```yaml
{
  <name-of-decorator>: {
    child:  <child-definition>
}
```

Other arguments can of course be specified in normal JSON fashion.
Keep in mind simple children can be defined without the `{}` - e.g.

```yaml
child: success
```

is a completely valid definition.

### Counter `counter`

Runs the child node a set number of times.

#### Parameters 

- `count` : int, the number of times the child should be executed

`SUCCESS`: when the child finishes with `SUCCESS` the n-th time in a row.

`FAILURE`: as soon as the child finishes with `FAILURE`.

### Invert `invert`

Returns the inverse of the state returned by the child.
Doesn't change `RUNNING`.

`SUCCESS`: when the child finishes with `FAILURE`.

`FAILURE`: when the child finishes with `SUCCESS`.

### Loop `loop`

Repeats the child node forever. Useful when controlled by overarching logic, else the tree can get stuck.

`RUNNING`: Forever.

### Timeout `timeout`

Runs the child (decorated node) for a set amount of time.
Can be used without specifying a child - that way, it will only serve as a 'pause' -
it'll return `RUNNING` for the duration specified in its argument.

#### Parameters

- `time` - float, the time to use for the timer in seconds (e.g. `time: 10.0`).

Without child:

`RUNNING`: while the timer is running.

`SUCCESS`: as soon as timer ended.

With child:

`RUNNING`: while the child is `RUNNING`.

`SUCCESS`: when the child finishes with `SUCCESS`.

`FAILURE`: when the child finishes with `FAILURE`.
