/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.minion.move;

import com.google.gson.annotations.SerializedName;
import org.terasology.entitySystem.Component;
import org.terasology.math.geom.Vector3f;
import org.terasology.navgraph.WalkableBlock;
import org.terasology.pathfinding.model.Path;

/**
 *
 */
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

