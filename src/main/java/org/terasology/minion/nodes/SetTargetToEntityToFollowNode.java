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
import org.terasology.minion.move.MinionMoveComponent;
import org.terasology.navgraph.WalkableBlock;
import org.terasology.pathfinding.componentSystem.PathfinderSystem;
import org.terasology.minion.components.FollowComponent;
import org.terasology.registry.In;

/**
 * Makes the actor follow the entity specified in the {@link FollowComponent}.
 */
public class SetTargetToEntityToFollowNode extends Node {
    @Override
    public SetTargetToEntityToFollowTask createTask() {
        return new SetTargetToEntityToFollowTask(this);
    }

    public static class SetTargetToEntityToFollowTask extends Task {

        @In
        private PathfinderSystem pathfinderSystem;

        public SetTargetToEntityToFollowTask(Node node) {
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
            Vector3f position = targetLocation.getWorldPosition();
            WalkableBlock block = pathfinderSystem.getBlock(position);
            if (block == null) {
                return Status.FAILURE;
            }
            MinionMoveComponent moveComponent = actor().getComponent(MinionMoveComponent.class);
            moveComponent.target = block.getBlockPosition().toVector3f();
            actor().save(moveComponent);
            return Status.SUCCESS;
        }

        @Override
        public void handle(Status result) {

        }

        @Override
        public SetTargetToEntityToFollowNode getNode() {
            return (SetTargetToEntityToFollowNode) super.getNode();
        }
    }
}
