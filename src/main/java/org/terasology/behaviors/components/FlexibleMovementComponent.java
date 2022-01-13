// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.components;

import com.google.common.collect.Lists;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.world.block.Blocks;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.List;

public final class FlexibleMovementComponent implements Component<FlexibleMovementComponent> {

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

    public void resetPath() {
        path.clear();
        pathIndex = 0;
    }

    public void advancePath() {
        pathIndex += 1;
        if (pathIndex < path.size()) {
            target = path.get(pathIndex);
        }
    }

    public boolean isPathFinished() {
        return pathIndex >= path.size();
    }

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
    public void copyFrom(FlexibleMovementComponent other) {
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
