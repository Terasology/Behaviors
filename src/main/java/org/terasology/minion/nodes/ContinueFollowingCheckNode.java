/*
 * Copyright 2016 MovingBlocks
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
package org.terasology.minion.nodes;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.behavior.tree.Node;
import org.terasology.logic.behavior.tree.Status;
import org.terasology.logic.behavior.tree.Task;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.pathfinding.componentSystem.PathfinderSystem;
import org.terasology.minion.components.FollowComponent;
import org.terasology.registry.In;
import org.terasology.rendering.nui.properties.Range;

/**
 * Checks if the actor still wants to move closer to a target specified by a {@link FollowComponent}.
 */
public class ContinueFollowingCheckNode extends Node {

    @Range(min = 0, max = 20)
    private float minDistance = 0.0f;

    @Range(min = 0, max = 100)
    private float maxDistance = 100.0f;

    @Override
    public ContinueFollowingCheckTask createTask() {
        return new ContinueFollowingCheckTask(this);
    }

    public static class ContinueFollowingCheckTask extends Task {
        @In
        private PathfinderSystem pathfinderSystem;

        public ContinueFollowingCheckTask(Node node) {
            super(node);
        }


        @Override
        public Status update(float dt) {
            FollowComponent followWish = actor().getComponent(FollowComponent.class);
            if (followWish == null) {
                return Status.FAILURE;
            }
            EntityRef entityToFollow = followWish.entityToFollow;
            if (entityToFollow == null || !entityToFollow.isActive()) {
                return Status.FAILURE;
            }
            LocationComponent targetLocation = entityToFollow.getComponent(LocationComponent.class);
            if (targetLocation == null) {
                return Status.FAILURE;
            }
            Vector3f targetPoint = targetLocation.getWorldPosition();

            LocationComponent currentLocation = actor().getComponent(LocationComponent.class);
            if (currentLocation == null) {
                return Status.FAILURE;
            }
            Vector3f currentPoint = currentLocation.getWorldPosition();

            float minDistanceSquared = getNode().getMinDistance() * getNode().getMinDistance();
            float maxDistanceSquared = getNode().getMaxDistance() * getNode().getMaxDistance();
            float currentDistanceSquared = currentPoint.distanceSquared(targetPoint);
            if (currentDistanceSquared <= minDistanceSquared) {
                return Status.FAILURE;
            }
            if (currentDistanceSquared >= maxDistanceSquared) {
                return Status.FAILURE;
            }

            return Status.SUCCESS;
        }

        @Override
        public void handle(Status result) {

        }

        @Override
        public ContinueFollowingCheckNode getNode() {
            return (ContinueFollowingCheckNode) super.getNode();
        }
    }

    public float getMinDistance() {
        return minDistance;
    }
    public float getMaxDistance() {
        return maxDistance;
    }
}
