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

    // acceptable distance from goal for completion
    public double pathGoalDistance = 0;

    public List<String> movementTypes = Lists.newArrayList("walking", "leaping");
    public boolean collidedHorizontally;
    public float lastInput;
    public int sequenceNumber;
    // immediate movement target
    public Vector3i target = new Vector3i();

    /**
     * The maximum distance from a target before it is considered to be reached
     */
    public float targetTolerance = 0.2f;

    // an entity to take the goal position from
    private EntityRef pathGoalEntity = null;

    // last known goal position
    private Vector3i pathGoalPosition = new Vector3i();

    // generated path to goal
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

    public void setPathGoal(EntityRef entity) {
        pathGoalEntity = entity;
        resetPath();
    }

    public void setPathGoal(Vector3i pos) {
        pathGoalPosition.set(pos);
        pathGoalEntity = null;
        resetPath();
    }

    public Vector3i getPathGoal() {
        if (pathGoalEntity != null && pathGoalEntity.getComponent(LocationComponent.class) != null) {
            Vector3f worldPosition = pathGoalEntity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f());
            Vector3i pos = Blocks.toBlockPos(worldPosition);
            pathGoalPosition.set(pos);
        }
        return pathGoalPosition;
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
        pathGoalPosition.set(other.pathGoalPosition);
        pathGoalDistance = other.pathGoalDistance;
        path = other.path;
        pathIndex = other.pathIndex; //TODO change me when migrate JOML
        movementTypes.clear();
        movementTypes.addAll(other.movementTypes);
        collidedHorizontally = other.collidedHorizontally;
        lastInput = other.lastInput;
        sequenceNumber = other.sequenceNumber;
    }
}
