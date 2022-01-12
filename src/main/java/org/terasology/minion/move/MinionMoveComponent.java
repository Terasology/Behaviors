// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion.move;

import org.joml.Vector3f;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.navgraph.WalkableBlock;
import org.terasology.pathfinding.model.Path;
import org.terasology.persistence.typeHandling.annotations.SerializedName;

public class MinionMoveComponent implements Component<MinionMoveComponent> {

    //TODO: Can/should we get rid of this movement type?
    //      If an actor should move along a path, we'll construct a (more complex) behavior like `dynamicPathfindingFollow.behavior`
    //      in which we set the target, compute the path, setup the actor before moving, and eventually move the along the path by
    //      subsequently calling the basic `move_to` child action.
    public enum Type {
        @SerializedName("direct")
        DIRECT,
        @SerializedName("path")
        PATH
    }

    public Type type = Type.DIRECT;
    public Vector3f target;
    public boolean targetReached;
    public boolean breaking;

    public transient Path path;
    public int currentIndex;

    public transient WalkableBlock currentBlock;
    /** Whether the actor encountered a {@link org.terasology.engine.logic.characters.events.HorizontalCollisionEvent HorizontalCollisionEvent} recently. */
    public transient boolean horizontalCollision;
    /** Whether the actor movement is supposed to be jumping. */
    public transient boolean jumpMode;
    /** Time frame in which the actor is supposed to enter {@link #jumpMode} (if greater 0). */
    public transient float jumpCooldown;

    @Override
    public void copyFrom(MinionMoveComponent other) {
        this.type = other.type;
        this.target = new Vector3f(other.target);
        this.targetReached = other.targetReached;
        this.breaking = other.breaking;
        this.path = other.path;
        this.currentIndex = other.currentIndex;
        this.currentBlock = other.currentBlock;
        this.horizontalCollision = other.horizontalCollision;
        this.jumpMode = other.jumpMode;
        this.jumpCooldown = other.jumpCooldown;
    }
}

