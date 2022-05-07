# Quick Start

This page serves as a brief summary of the main points of the Behavior system.
The details of each part are detailed on their respective pages.

For this tutorial, we'll assume you have an entity/creature you want to bring to life.
If you don't, check out our [asset system tutorial](https://github.com/Terasology/TutorialAssetSystem/wiki/Add-New-Creature) to learn how to create one.

## Behavior trees

Behavior trees are the structures used to describe the behavior of an entity within the game.
A behavior is a pre-defined set of actions performed by an entity, triggered by specific conditions or events.
Behaviors can be related to movement (animals wandering in an open field) or more complex actions (searching for water sources to fulfill a specific need). 

In Terasology, a behavior tree is described in a JSON-like file, which contains pre-defined elements (nodes) and actions associated with these nodes.
These files (`.behavior`) are located in the `assets/behaviors` folder of each module.
Behavior trees are composed of objects implementing the `TreeNode` interface.

> [!WARNING]
> Note, that the syntax of behavior tree files is **not valid JSON** in all cases.

To learn more about the structure of behavior trees, please see [Tree Nodes](Tree-Nodes).

To see how a behavior tree can respond to events and changes in the entity, see [Responding to Outside Changes](Responding-to-Outside-Changes).

If you want to create your own Behavior (`.behavior`) file, you can learn how to do it [here](Building-A-Behavior-Tree).

## Assigning a Behavior to an Entity

To possess a behavior, an entity must have a `Behavior` component referring to an existing behavior tree.
To specify the behavior tree your entity will use, go into its `.prefab` file, and find (or create) the `Behavior` entry in the JSON.

The correct format is:

```json
"Behavior" : {
    "tree" : "<module>:<behavior>"
}
```

You can use any pre-made behavior existing in another module.
If you use the `stray` behavior from the Behaviors module, for example, your `.prefab` behavior entry will be:

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

## Groups

Behaviors can also be used by groups of entities.
Similarly to behaviors, a group is described by JSON-like files (`.group`) are located in the `assets/groups` folder of each module.
Groups can be used in situations where the same behavior must be assigned to multiple entities at a time. For more details on groups, please see [Groups](Groups).

## GUI editor

Behavior trees can also be created or edited through the use of the Behavior GUI editor.
The GUI editor is currently unstable, but its latest version can be accessed in-game through the <kbd>F5</kbd> key.

> [!NOTE]
> The UI editor is in a highly experimental state.
> We recommend that you edit your `.behavior` files directly in a text editor of your choice.

## Examples

If you want to learn how to build your own behavior, please check the [Step-by-Step Tutorial](Step-By-Step-Tutorial). 

Also - if you want to start by learning how you can reuse existing code by modifying existing behaviors and adapting it to your scenario - there's a [case study](Case-Study) about it available.
