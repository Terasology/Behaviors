# Groups

Here you can find some information about Groups, and how they are used in conjunction with Behaviors.
To understand Groups, however, it is important to consider 

<ol type="i">
<li> how collective behaviors can be manifested, and 
<li> what are scenarios where groups can be used with behaviors.
</ol>

It is important that you have a basic comprehension of how the behavior system works (especially the concept of behavior trees and associated states); if you didn't do it yet, please see the [Big Picture](Big-Picture) overview.

## Scenarios

There are different ways to look at collective behaviors, but in a general manner, we can classify it from the _coordination_ perspective (you can learn more about this [here](https://casals.io/code/gsoc-reaching-first-milestone/)).
From the game perspective, let us consider three different scenarios where an entity joins a group:

* Scenario 1: Entities that join a group but are independent in terms of behavior (even if they have the same behavior tree - which may not be the case).

* Scenario 2: Entities that join a group and have the same behavior tree, and they have to be _somewhat_ coordinated. A typical illustration is a group where only one of the members perceives an event, but all group members react to it. Their reactions, however, are processed individually.

* Scenario 3: Entities that join a group and the same behavior tree, and they _must_ act the same way. In this scenario, the entities completely lose their autonomy, and their behavior is perfectly synchronized.

You can read more about the different group behavior scenarios [here](https://casals.io/code/gsoc-reaching-second-milestone/). 

## Implementation

In Terasology, the group structure is implemented in a manner that it can cover all three scenarios described above.

### Assets

Similarly to behaviors, a group is described by JSON-like files (`.group`) are located in the `assets/groups` folder of each module:

<fig src="images/Groups/groups-tree-folder.png" alt="Group assets">Groups as part of a module's assets.</fig>

A `.group` file describes the characteristics of a group as:

- `groupLabel`: the unique group identifier.
- `needsHive`: flags the need for a _hivemind_ structure (when group members need to behave in unison, like in Scenario 3 described above).
- `behavior`: the name of the behavior tree used by the group (trees from other modules can be used as long as they are listed as dependencies).

This structure is represented within the file as:

```yaml
{
  "groupLabel": "<group-label>",
  "needsHive": "<boolean>",
  "behavior": "<module:behavior>"
}
```

### Components

There are two main components used by Groups: `GroupTag` and `GroupMind`. The `GroupTag` component is used to identify an entity as part of a labeled group.
It can be inserted in an entity's prefab file so that every time an instance of that entity is spawned in the game it will already be marked as belonging to the same group: 

```yaml
"GroupTag" : {
     "groups" : [ "<group-label>" ]
   }
```

Observe that the `groups` component field is an array.
This is because the same entity can belong to many groups at the same level; the logic behind how the behavior inherited from each group will be enforced is handled at the system level.

This component possesses two other fields, meant to be used in specific scenarios: `backupBT`, which stores an instance of a behavior tree object, and `backupRunningState`, which stores a behavior tree interpreter associated with `backupBT`. 
Please check the Usage section below to see how they are meant to be used.

The `GroupMind` component complements `GroupTag` in cases where there's the need for a unison behavior by all entities belonging to a group (please see Scenario 3 above).
It possesses three distinct fields:

- `groupLabel`: the unique group identifier with which the component will be associated.
- `behavior`: the name of the behavior tree used by the group (trees from other modules can be used as long as they are listed as dependencies).
- `groupMembers`: a set containing the identifiers for each of the entities belonging to the group. 

This last field exists because, in scenarios that require `GroupMind`, there is a super-entity that manages all of the other entities within the group (this is better exemplified in the Usages section below).

### Collective Behavior System 

Establishing a unison behavior also requires that the behavior trees of all entities within the group have not only the same behavior tree but the same behavior state at all times.
For this reason, the `CollectiveBehaviorSystem` was implemented in the core package.
It allows the use of a `CollectiveInterpreter` that processes all the behavior trees at the same time, assuring that all entities from a group will have the same state at all times.   

## Usage

Using the group components in Terasology means identifying which of the three scenarios described above is appropriate to your case, and applying the correspondent usage patterns - which are:

- Scenario 1: in this case, entities belonging to a group only need to be identified as so. The group entities will then only use the `GroupTag` component, which will be updated whenever they join or leave a different group.

- Scenario 2: in this case entities belonging to a group need to possess the same behavior tree, regardless of the reasons. The group entities will still only use the `GroupTag` component. However, when an entity joins a group, it might be necessary to preserve its original behavior tree and associated state. In that case, both are saved into the appropriate `GroupTag` fields (`backupBT` and `backupRunningState`, respectively).

- Scenario 3: in this case entities belonging to a group need to behave in unison. This can be achieved by electing a leader within the group, or by creating an invisible super-entity that will determine the group behavior. Regardless of how it is created, the leader or super-entity will need the `GroupMind` component. 

You can also check the [step-by-step tutorial](Step-By-Step-Tutorial) for a quick overview of how to use groups in your module.
There is also a [case study](Case-Study) on creating a module and adding behavior to creatures in a group.
