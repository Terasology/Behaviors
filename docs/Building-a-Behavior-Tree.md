# Creating a Behavior Tree

Any given behavior tree is specified as a JSON-like asset in the module's `assets/behaviors` folder, with the `.behavior` extension:

<fig src="images/Building-A-Behavior-Tree/behavior-tree-folder.png" alt="Behavior file assets in the module asset file tree">Behavior files are part of a module's assets.</fig>

To create a new tree, just create a `<name>.behavior` file in your module's `assets/behaviors` folder and open it in your favorite text editor.

## Tree file structure

The actual tree definition files have a very simple structure: essentially, the `.behavior` file only represents the definition of the root (top-most) TreeNode, with all of its children/arguments, recursively.
As an example, this is how the file `still.behavior` (originally from the `WildAnimalsMadness` module) is structured:

```yaml
{
  sequence:[
    {
      animation : {
        play: "engine:Stand.animationPool",
        loop: "engine:Stand.animationPool"
      }
    },
    {
      sleep : {
        time : 3
      }
    }
  ]
}
```

### Nodes - the building blocks

Every TreeNode is required to have a `name`.
That name is pre-defined for the logic / flow control nodes and some special nodes - a reference of which is [here](https://github.com/Terasology/Behaviors/wiki/Control-Flow-Nodes) - and every Action and Decorator node has its name specified by the `name` field in its `BehaviorAction` annotation - more on that [here](Tree-Nodes).

This `name` represents any and every Node in the JSON defining the tree.

#### Simple Nodes, Actions

A simple node with no arguments - which includes most Actions - is represented only by writing down its name:

```yaml
<node-name>
```

#### Nodes with arguments

A node which needs some arguments to be specified needs to be written down as an object:

```yaml
{ <node-name>: { argument-1: value-1, ? <..>
        argument-n
      : value-n } }
```

In the case of **Decorators** (nodes with strictly 1 child), the child is provided as any other argument, in the form

```yaml
child: <node-definition>
```

where `<node-definition>` can be any arbitrary node, including composite nodes or other Decorators.

#### Composite Nodes

A Composite (multiple-children) node is represented using an array:

```yaml
{ <composite-node-name>: [
      <child-node-1>,
      <child-node-2>,
      <..>
      <child-node-3>,
    ] }
```

```yaml
success
```

> [!NOTE|label:A note on quotes and JSON]
> While the JSON standard disallows keys not wrapped in double quotations, our GSON / deserialization implementation for the most part doesn't care about it - at least in terms of behavior tree files.
> This means that this:
> 
> ```yaml
> { sequence: [{ timer: { time: 5 } }, { log: { message: Hello! } }] }
> ```
> 
> is virtually identical to this:
> 
> ```json
> {
>    "sequence": [
>     {
>       "timer": {"time": 5}
>     },
>     {
>       "log": {"message": "Hello!"}
>     }
>   ]
> }
> ```
> 
> The variant without quotes is a bit more readable and less 'boilerplate-y', but both variants are possible.
> 
> _However_, the canonical - quote-infested - version is the only one 100% officially supported, so if you run into issues with the tree file loading, it is a good idea to try if the issue persists with a canonical JSON version of the same tree.
> 
> One very handy tool for converting the terse version into canonical JSON is [YAML Parser](http://yaml-online-parser.appspot.com/) - simply paste your tree definition into the left pane, and it spits out canonical JSON on the right.
> It also catches many formatting / syntax errors (but not all, as YAML is a superset of JSON).

## Examples

You can find examples of behavior trees in action [here](Pre-made-Behaviors-and-Nodes).
