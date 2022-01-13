// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.components;

import org.joml.Vector3f;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.navgraph.WalkableBlock;
import org.terasology.pathfinding.model.Path;
import org.terasology.persistence.typeHandling.annotations.SerializedName;

public class MinionMoveComponent implements Component<MinionMoveComponent> {

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
    public transient boolean horizontalCollision;
    public transient boolean jumpMode;
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

