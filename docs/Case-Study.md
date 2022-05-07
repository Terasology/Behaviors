# Case Study

This page details the process of creating a new module with creatures, behaviors, and groups.
You should understand how Terasology's behavior system works - it is explained [here](big-picture).
All source code for the present tutorial is available in the [TutorialGroups](https://github.com/Terasology/TutorialGroups/) module.

## Context

We are going to show how to create a module from scratch, but using existing creatures and behaviors as a basis.

**This is a long tutorial as we will explore all steps of the process in detail**.

The idea is to show not only how behaviors and groups can be used together, but how to reuse existing code in new modules.
For this reason, we will use existing code as much as possible - be sure to follow the links as they appear.

Our objective is to create a group of creatures with the same behavior.
We will use a variant of deer from the `WildAnimals` module for that.
Also, for this case study:

- We will use existing creatures from the [WildAnimals module](https://github.com/Terasology/WildAnimals/) 
- We assume that you already prepared your development workspace, as described [here](https://github.com/MovingBlocks/Terasology/wiki/Contributor-Quick-Start)
- We are creating a new module in this tutorial, but you can also [use an existing module as a basis](https://github.com/MovingBlocks/Terasology/wiki/Developing-Modules). 
- Our module depends on other existing modules.

If you don't know how to include other modules as dependencies of a new one, please check [this page](https://github.com/MovingBlocks/Terasology/wiki/Module-Dependencies).

## Getting started

We will create a new module called `TutorialGroups` and add `Behaviors` and `WildAnimals` as dependencies.
Your `module.txt` file should look like this:

```json
{
    "id" : "TutorialGroups",
    "version" : "0.1.0-SNAPSHOT",
    "author" : "casals",
    "displayName" : "TutorialGroups",
    "description" : "Tutorial module for the wiki case study",
    "dependencies" : [
            {"id" : "Behaviors", "minVersion" : "0.2.0"},
            { "id": "WildAnimals", "minVersion": "0.2.0" }
        ],
    "serverSideOnly" : false
}

``` 

Let us also create a new entity to use in our tests.
Since our module depends on `WildAnimals`, we can use any of the existing deer as a basis for our new entity (if you don't know how entities can be described by prefabs, please check [this link](https://github.com/MovingBlocks/Terasology/wiki/Entity-System-Architecture#prefabs)).
We will create an entity called `testDeer`, based on the `greenDeer` prefab.
For that, we just need to create a file named `testDeer.prefab` in our `assets/prefabs` folder, with the following contents:

```json
{
  "parent" : "greenDeer"
}
```

This will allow our `testDeer` to inherit all base components from `greenDeer`.

## Defining a behavior

There are several different behaviors that we could add to our deer.
Usually, we would just include a `Behavior` component in the entity prefab, as shown [here](Quick-Start#assigning-a-behavior-to-an-entity) (you can learn how to create your behaviors [here](Step-By-Step-Tutorial)).
However, since we want a broad example of how to use behaviors and groups, let's assume the following situation:

- We want our deer to be assigned to groups as soon as they are spawned;
- We want our deer to idly stray around until they are attacked by a player (in which case they flee away); and
- We want our deer to display group coordination: if one deer is attacked, all deer in the group should flee away from the group.

If we consider only the first two items of our behavior checklist, there's already a behavior that does that - it is called `critter`, and it is defined in the `Behaviors` module.
Let us take a closer look at the  `critter.behavior` file:

```json
{
  dynamic: [
      {
        guard: {
          componentPresent: "Behaviors:Fleeing",
          child: {
            sequence: [
            check_flee_stop,
            {
              lookup: {
                tree: "Behaviors:flee"
              }
            }
            ]
          }
        }
      },
      {
        lookup: {
          tree: "Behaviors:stray"
        }
      }

  ]
}
```

The behavior tree described in this file can be interpreted as:

- There is a dynamic selector on the top of the tree that constantly re-evaluates all children that previously returned `FAILURE` with each tick;
- The first sub-node checks if the entity possesses a component called `Fleeing`, in which case it executes an action (`check_flee_stop`) and a nested behavior tree (`Behaviors:flee`);
- The second sub-node calls another nested behavior tree (`Behaviors:stray`).

You can read more about each of these node types [here](Control-Flow-Nodes).
The nested sub-trees are pretty descriptive in what they do: the first one is composed by a set of nodes and actions that makes the entity to flee away from whoever is causing them damage, and the second one just establishes a straying behavior.
Also, the `check_flee_stop` action is implemented in the `Behaviors` module by the `CheckFleeStopAction`, and its goal is to determine if the damage received came from a player (*instigator*), thus determining if the entity should flee or not.

Note that due to the dynamic node selector this tree will be re-evaluated every time it's determined that the entity should **not** flee (in which case the action returns a `FAILURE`) status.
However, for this check to happen, the entity must possess the `Fleeing` component.
Now - let's look at the original `deer` prefab (used as a basis for all deer in the `WildAnimals` module:

```json
{
  "skeletalmesh" : {
    "mesh" : "deer",
    "heightOffset" : -0.8,
    "material" : "deerkin",
    "animation" : "deerIdle",
    "loop" : true
  },
  "Behavior" : {
    "tree" : "Behaviors:critter"
  },
  "FleeOnHit" : {
    "minDistance" : 5
  },
//...
}
```

Observe that the prefab does not contain any reference to a component named `Fleeing` - however, it contains a component called `FleeOnHit`, which seems related to what we need.
Also - it is important to notice that this prefab was defined in the `WildAnimals` module, but there are no references to any component called `FleeOnHit`.
This happens because `WildAnimals` also depends on the module `Behaviors`; when your module depends on another one, you can access all of its components and assets.
In the case of assets, you need to specify its module of origin (as in `Behaviors:stray`), but components can be seamlessly referenced and used in your prefabs. 

With that in mind, let's go back to the `Behaviors` module and take a closer look at the `FleeOnHit` component.
Its implementation (`FleeOnHitComponent` class) is pretty simple: it defines a minimum safe distance to be maintained from the aggressor, and a speed multiplier parameter used to set a default running speed when fleeing.
This component is used by a system implemented by `BehaviorsEventSystem`:

```java
@RegisterSystem(RegisterMode.AUTHORITY)
public class BehaviorsEventSystem extends BaseComponentSystem {

    @In
    private Time time;

    @ReceiveEvent(components = FleeOnHitComponent.class)
    public void onDamage(OnDamagedEvent event, EntityRef entity) {

        // Make entity flee
        FleeingComponent fleeingComponent = new FleeingComponent();
        fleeingComponent.instigator = event.getInstigator();
        fleeingComponent.minDistance = entity.getComponent(FleeOnHitComponent.class).minDistance;
        entity.saveComponent(fleeingComponent);

        // Increase speed by multiplier factor
        CharacterMovementComponent characterMovementComponent = entity.getComponent(CharacterMovementComponent.class);
        characterMovementComponent.speedMultiplier = entity.getComponent(FleeOnHitComponent.class).speedMultiplier;
        entity.saveComponent(characterMovementComponent);

    }
//...
}
```

There are a few things going on this system.
We won't explore how events, systems, and components work together at this moment, but you can learn more about the Entity System architecture used in Terasology [here](https://github.com/MovingBlocks/Terasology/wiki/Entity-System-Architecture).
Let's take a closer look at the `onDamage` method (which defines what happens to an entity possessing the `FleeOnHit` component once it receives damage):

- When the entity is damaged, the system creates a new `Fleeing` component and attaches it to the entity;
   - The new component possesses relevant information about who caused the damaged (which is used later, as we saw before);
- The entity's speed is altered according to the speed multiplier defined in the `FleeOnHit` component.

This pretty much solves what we need in terms of behavior, but we still need to make all deer in the group to flee if one of them is attacked.
At the same time, the fleeing behavior can be the same one already implemented in the `Behaviors` module (we just need to attach the group conditions).
That means we need:

- A way of identifying our group; and
- A way to make all entities within the group to flee if one of them is attacked.

Groups are, in a nutshell, an easy manner of identifying and handling multiple entities - you can read more about it [here](Groups).
For now, let's define that our group will be called `wisedeer`.
As we said before, we want our deer to belong to this group as soon as they are spawned - so we will amend our `testDeer.prefab` file to include this information.
We also want our deer to have a *slightly different* fleeing behavior from the one we saw before - but we have already seen a good strategy on processing a damage event, so let's use that.
We will also create a modified component called `GroupFleeOnHit` - it's pretty much the same as the original, but we want to take advantage of the existing implementation without interfering with other modules or behaviors using the same component.
Also, it's a great opportunity to add new characteristics.

All things considered, we will need:

- A new implementation of the modified `GroupFleeOnHit` component; and
- A system implementation, similar to the `BehaviorsEventSystem`, to process what happens in case of damage.

Note that we can assign behaviors to entities either in their prefabs, or - if we are using groups - in our group asset (another JSON-like file).
We want to keep this tutorial as simple as possible, so we won't use any group assets at this point - but you can read more about them in the [Groups wiki page](Groups).

At this point, it is important to notice that there is a reason why we are using the `greenDeer` as our basis.
All RGB deer in the `WildAnimals` module were built to be used as examples in tutorials; in particular, the `greenDeer` does not possess the `FleeOnHit` component.
This is important since the `GroupFleeOnHit` component exists so we can reuse as much code as possible (including the original `critter` behavior and `Fleeing` component).
In the case an entity possessed both components, we might have conflicting scenarios caused by different superposing settings (such as the speed multiplier).
We could also implement a new behavior from scratch, of course - but this case is already covered [here](Step-By-Step-Tutorial), and we don't want this tutorial to get repetitive. 

## Implementation

With all the considerations above, our new `testDeer.prefab` file should look like this:

```json
{
  "parent" : "greenDeer",
  "Behavior" : {
    "tree" : "Behaviors:critter"
  },
  "GroupFleeOnHit" : {
    "minDistance" : 5
  },
  "GroupTag" : {
       "groups" : [ "wisedeer" ]
     }
}
```

We will also need two new classes for our new component and system, respectively.
Let's create them in two different packages:

#### `GroupFleeOnHitComponent`:

```java
package org.terasology.behaviors.components;

import org.terasology.entitySystem.Component;

/**
 * If this component is attached to an NPC entity it will exhibit the flee-on-hit behavior
 * When hit, the NPC will run with a speed of `speedMultiplier`*normalSpeed
 * till it is at a safe `minDistance` from the damage inflicter- `instigator`.
 * When it reaches a safe distance the instigator is set to null. This component uses
 * @see FleeOnHitComponent as a reference/basis.
 */
public class GroupFleeOnHitComponent implements Component {
    /* Minimum distance from instigator after which the NPC will stop 'flee'ing */
    public float minDistance = 10f;
    /* Speed factor by which flee speed increases */
    public float speedMultiplier = 1.2f;
}
```

#### `GroupBehaviorsEventSystem`:

```java
package org.terasology.behaviors.system;
///import ...
/*
 * Listens for damage events and responds according to the group behavior desired
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class GroupBehaviorsEventSystem extends BaseComponentSystem {

    @In
    private Time time;
    @In
    private EntityManager entityManager;

    @ReceiveEvent(components = GroupFleeOnHitComponent.class)
    public void onDamage(OnDamagedEvent event, EntityRef entity) {

        //Get all entities belonging to the 'wisedeer' group:
        for (EntityRef entityRef : entityManager.getEntitiesWith(GroupTagComponent.class)) {
            if (entityRef.getComponent(GroupTagComponent.class).groups.contains("wisedeer")) {
                // Make entity flee
                FleeingComponent fleeingComponent = new FleeingComponent();
                fleeingComponent.instigator = event.getInstigator();
                fleeingComponent.minDistance = entityRef.getComponent(GroupFleeOnHitComponent.class).minDistance;
                entityRef.saveComponent(fleeingComponent);

                // Increase speed by multiplier factor
                CharacterMovementComponent characterMovementComponent = entityRef.getComponent(CharacterMovementComponent.class);
                characterMovementComponent.speedMultiplier = entityRef.getComponent(GroupFleeOnHitComponent.class).speedMultiplier;
                entityRef.saveComponent(characterMovementComponent);
            }
        }
    }
}
```

Observe that there is one difference in our system when compared to the original `BehaviorsEventSystem` class: it listens to damage events according to the existence of our new component, and then assigns a new `Fleeing` component to each of the entities belonging to the group.

## Conclusion

In this tutorial, we showed how to reuse creatures and behaviors from other modules to create your own group of creatures.
Learning about all the different mechanisms in Terasology can be challenging, especially if this is your first time working with this kind of architecture.
There are many other different manners of using behaviors and groups; besides the links and modules used here, we recommend that you check the [WildAnimalsMadness](https://github.com/Terasology/WildAnimalsMadness/) module. 
