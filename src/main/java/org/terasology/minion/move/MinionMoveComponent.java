// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion.move;

import org.joml.Vector3f;
import org.terasology.entitySystem.Component;
import org.terasology.navgraph.WalkableBlock;
import org.terasology.pathfinding.model.Path;
import org.terasology.persistence.typeHandling.annotations.SerializedName;

public class MinionMoveComponent implements Component {
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

}

