# Behaviors and Nodes

This is a curated list with some of the more specialized Nodes and Behaviors that are available out of the box in different modules.

- [Behaviors and Nodes](#behaviors-and-nodes)
  - [Behaviors](#behaviors)
    - [DoRandomMove](#dorandommove)
    - [Stray](#stray)
    - [Follow](#follow)
    - [AttackFollowedEntity](#attackfollowedentity)
    - [Hostile](#hostile)
    - [Flee](#flee)
  - [Nodes](#nodes)
    - [Control Nodes](#control-nodes)
    - [Movement Nodes](#movement-nodes)
    - [Sound Nodes](#sound-nodes)

## Behaviors

You can find the premade behavior files in [this folder](https://github.com/Terasology/Behaviors/tree/1635a7734e269bedc8d92387942d8dd1468f3430/assets/behaviors/common).

Examples of these pre-made behaviors being used to create new creature behaviors can be found in [this folder](https://github.com/Terasology/Behaviors/tree/1635a7734e269bedc8d92387942d8dd1468f3430/assets/behaviors/creatures).

### [DoRandomMove](https://github.com/Terasology/Behaviors/blob/1635a7734e269bedc8d92387942d8dd1468f3430/assets/behaviors/common/doRandomMove.behavior)

Short, utilitarian tree. When ran, finds a nearby block and moves towards it.

### [Stray](https://github.com/Terasology/Behaviors/blob/1635a7734e269bedc8d92387942d8dd1468f3430/assets/behaviors/common/stray.behavior)

A basic tree for critters. The entity starts the Walk animation, executes the `do_random_move` tree, then waits a while in the Stand animation, then repeats.
Results in randomly wandering mobs.

### [Follow](https://github.com/Terasology/Behaviors/blob/1635a7734e269bedc8d92387942d8dd1468f3430/assets/behaviors/common/follow.behavior)

The entity finds the nearest player and moves towards them if they are within a minimum distance (using the [`set_target_to_followed_entity`](https://github.com/Terasology/Behaviors/blob/master/src/main/java/org/terasology/behaviors/actions/SetTargetToFollowedEntityAction.java)).

### [AttackFollowedEntity](https://github.com/Terasology/Behaviors/blob/1635a7734e269bedc8d92387942d8dd1468f3430/assets/behaviors/common/attackFollowedEntity.behavior)

The entity searches for a player to follow within a range. If it finds one, it attacks it using the [`damage_followed_entity`](https://github.com/Terasology/Behaviors/blob/master/src/main/java/org/terasology/behaviors/actions/DamageFollowedEntityAction.java) action.

### [Hostile](https://github.com/Terasology/Behaviors/blob/1635a7734e269bedc8d92387942d8dd1468f3430/assets/behaviors/common/hostile.behavior)

This behavior uses the `AttackFollowedEntity` behavior and the [`check_attack_stop`](https://github.com/Terasology/Behaviors/blob/1635a7734e269bedc8d92387942d8dd1468f3430/src/main/java/org/terasology/behaviors/actions/CheckAttackStopAction.java) action. If the player is out of the critter's range, it will stop attacking.

### [Flee](https://github.com/Terasology/Behaviors/blob/1635a7734e269bedc8d92387942d8dd1468f3430/assets/behaviors/common/flee.behavior)

The entity runs away from the player using the [`set_target_nearby_block_away_from_instigator`](https://github.com/Terasology/Behaviors/blob/master/src/main/java/org/terasology/behaviors/actions/SetTargetToNearbyBlockAwayFromInstigatorAction.java) action. A use for this might be if a critter is attacked, it might want to flee from its attacker.

## Nodes

These nodes can be found in [this folder](https://github.com/Terasology/Behaviors/tree/1635a7734e269bedc8d92387942d8dd1468f3430/assets/prefabs/behaviorNodes).

### Control Nodes

The `target` referred to in the following nodes is the `target` variable of the entity's `MinionMoveComponent`.

#### SetTargetLocalPlayerNode

Sets the target to the block the player is currently standing on.

#### SetTargetToNearbyBlockNode

Sets the target to a random reachable block near the entity.

### Movement Nodes

#### FindPathToNode

Requests a path towards a block from the Pathfinding system. This Path will be saved as `path` in the entity's MinionMoveComponent.

`RUNNING` while the path is being searched for

`SUCCESS` once a path has been found

`FAILURE` if a path can't be found

#### MoveToNode

Moves to the target specified in MinionMoveComponent.

`RUNNING` while the actor is moving towards the target
`SUCCESS` when the target is reached

#### MoveAlongPathNode

Moves along the `path`.

#### JumpNode

Triggers a single jump into the air.

`RUNNING` while the jump is in progress

`SUCCESS` once the actor lands

### Sound Nodes

#### `PlaySound`

_Properties_: `sound`, `volume`

Plays a sound (wow, really?)

`RUNNING`: while the sound is playing

`SUCCESS`: once sound ends playing

`FAILURE`: otherwise

#### `PlayMusic`

_Properties_: `music`

Starts playing music

`RUNNING`: while music is playing

`SUCCESS`: once the music ends playing

`FAILURE`: otherwise
