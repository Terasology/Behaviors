// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.components;

import com.google.common.collect.Lists;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.world.block.Blocks;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.List;

public final class MinionMoveComponent implements Component<MinionMoveComponent> {

    //TODO: why do we consider this to be a good default?
    //      would it make sense to modify the walkingPlugin in a way that it allows to "leap" 1 block up by default (without requiring the
    //      leaping plugin?) the fact that we have to "leap" is mostly just due to being a blocky world, but actually we're "walking up the
    //      hill", not "jumping up the hill" (kinda like minecraft's auto-step-up feature, just for mobs).
    public List<String> movementTypes = Lists.newArrayList("walking", "leaping", "falling");

    public boolean collidedHorizontally;
    public float lastInput;
    public int sequenceNumber;

    // immediate movement target
    //TODO: why is this public? who should change this?
    //      it feels like this is actually an INTERNAL control value, and having behaviors or systems tinker with like they do now can
    //      easily mess up any guarantees we would like to give for the fields on this component
    public Vector3i target = new Vector3i();

    /**
     * The maximum distance from the final goal before it is considered to be reached.
     *
     * TODO: should we use double or float here (cf. targetTolerance)
     */
    public double goalTolerance = 0;

    /**
     * The maximum distance from a target before it is considered to be reached
     */
    public float targetTolerance = 0.1f;

    // last known goal position
    private Vector3i goalPosition = new Vector3i();

    // an entity to take the goal position from
    //TODO(skaldarnar): What's the difference between MinionMoveComponent#pathGoalEntity and FollowComponent#entityToFollow?
    //                  How do we keep them in sync? Where are systems involved, and where to behaviors update respective fields?
    //                  Personally, I don't think this should be here...
    private EntityRef pathGoalEntity = null;

    /**
     * The path of reachable waypoints to the {@link #goalPosition}.
     *
     * If empty no path to the target exists, or it was not computed yet.
     * If the {@link #goalPosition} is reachable, the path will contain at least that position as last element.
     */
    private List<Vector3i> path = Lists.newArrayList();

    // current index along path above
    private int pathIndex = 0;

    //TODO: pathfinding related fields
    //        goalPosition      : final target position
    //        goalTolerance     : maximum distance from 'goalPosition'
    //        target            : next waypoint to move to (from 'path', eventually 'goalPosition'
    //        targetTolerance   : maximum distance from 'target'
    //        path              : list of waypoints to reach 'goal'
    //        pathIndex         : current position (or next target position?)

    //TODO: don't mention `pathIndex` in docstrings as they should be an internal detail?

    /**
     * Denote that the given {@code entity}'s location determines the final movement goal.
     *
     * This will clear the current {@link #path} and reset the {@link #pathIndex}.
     *
     * Note: Setting the path goal to an entity does NOT compute a path or set the {@link #goalPosition} vector. Only {@link #getPathGoal()}
     *       will take the entity into consideration when computing the actual path goal.
     *
     * @param entity the entity to move to; must have a {@link LocationComponent}
     */
    public void setPathGoal(EntityRef entity) {
        //TODO: ensure that entity is not EntityRef.NULL?
        pathGoalEntity = entity;
        resetPath();
    }

    /**
     * Set the final movement goal to the given {@code pos}.
     *
     * This will clear the current {@link #path} and reset the {@link #pathIndex}. It will also set the {@link #pathGoalEntity} to {@code null}.
     *
     * Note: Setting the path goal does NOT compute a path.
     *
     * @param pos the final movement goal position
     */
    public void setPathGoal(Vector3i pos) {
        goalPosition.set(pos);
        pathGoalEntity = null;
        resetPath();
    }

    /**
     * Retrieve the final movement goal position.
     *
     * If the {@link #pathGoalEntity} is set, the goal position is derived from the entity's location.
     * Otherwise, the goal position is {@link #goalPosition}.
     *
     * @return the current goal position
     */
    public Vector3i getPathGoal() {
        //TODO: this is logic on component, doing a lookup on a different entity.
        //      this is definitely not the recommended way to handle components and dependencies between entities.
        if (pathGoalEntity != null && pathGoalEntity.getComponent(LocationComponent.class) != null) {
            Vector3f worldPosition = pathGoalEntity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f());
            Vector3i pos = Blocks.toBlockPos(worldPosition);
            goalPosition.set(pos);
        }
        return goalPosition;
    }

    /**
     * Clear the stored {@link #path} and reset the {@link #pathIndex} to 0.
     */
    public void resetPath() {
        path.clear();
        pathIndex = 0;
    }

    /**
     * Increment the {@link #pathIndex} by one set the next {@link #target}, if it exists.
     *
     * TODO: should this just include whether the path is finished check?
     */
    public void advancePath() {
        pathIndex += 1;
        if (pathIndex < path.size()) {
            target = path.get(pathIndex);
        }
    }

    /**
     * Whether the entity reached the end of the {@link #path}.
     *
     * Note: finishing the path does not necessarily mean that the entity reached the goal position!
     */
    public boolean isPathFinished() {
        return pathIndex >= path.size();
    }

    /**
     * Store given path and reset {@link #pathIndex} pointer.
     * Also set {@link #target} to the first waypoint if the given path is not empty.
     *
     * TODO: Why reset pathIndex, but not the overall pathGoal? Or at least ensure that the given path leads to pathGoal?
     *       However, pathGoal may be irrelevant if pathGoalEntity is set... this is confusing...
     *
     * @param path the new path the entity should move along.
     */
    public void setPath(List<Vector3i> path) {
        resetPath();
        this.path.addAll(path);
        if (pathIndex < path.size()) {
            target = path.get(pathIndex);
        }
    }

    public List<Vector3i> getPath() {
        return path;
    }

    public int getPathIndex() {
        return pathIndex;
    }

    @Override
    public void copyFrom(MinionMoveComponent other) {
        target.set(other.target);
        targetTolerance = other.targetTolerance;
        pathGoalEntity = other.pathGoalEntity;
        goalPosition.set(other.goalPosition);
        goalTolerance = other.goalTolerance;
        path = other.path;
        pathIndex = other.pathIndex; //TODO change me when migrate JOML
        movementTypes.clear();
        movementTypes.addAll(other.movementTypes);
        collidedHorizontally = other.collidedHorizontally;
        lastInput = other.lastInput;
        sequenceNumber = other.sequenceNumber;
    }
}
