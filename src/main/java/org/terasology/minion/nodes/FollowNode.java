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
import org.terasology.logic.characters.CharacterMoveInputEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Quat4f;
import org.terasology.math.geom.Vector3f;
import org.terasology.minion.move.MinionMoveComponent;
import org.terasology.navgraph.NavGraphSystem;
import org.terasology.navgraph.WalkableBlock;
import org.terasology.pathfinding.componentSystem.PathfinderSystem;
import org.terasology.minion.components.FollowComponent;
import org.terasology.minion.components.NPCMovementComponent;
import org.terasology.pathfinding.model.Path;
import org.terasology.registry.In;

import java.util.Arrays;
import java.util.List;

/**
 * Updates the targetPosition field of the {@link NPCMovementComponent} to make it follow the entity specified by the
 * {@link FollowComponent}. The target field does not necessarly set directly to the location of the entitiy to follow
 * but to the next sub target on the path to the entity to follow. When the entity to follow moves, the path will be
 * recalculated and the target gets updated.
 *
 * It is just a proof of concept. It will propably be later on generalized (movement to fixed point should be supported
 * too, to avoid code duplication) and moved to the pathfinding module.
 */
public class FollowNode extends Node {

    @Override
    public FollowTask createTask() {
        return new FollowTask(this);
    }

    public static class FollowTask extends Task {
        private volatile Path nextPath;
        private Path path;
        private int currentIndex;
        private boolean calculatingPath;

        @In
        private NavGraphSystem navGraphSystem;
        @In
        private PathfinderSystem pathfinderSystem;

        public FollowTask(FollowNode node) {
            super(node);
        }


        @Override
        public void onInitialize() {
            path = null;
            nextPath = null;
            currentIndex = 0;
            calculatingPath = false;
        }

        @Override
        public Status update(float dt) {
            Status status = updateWithoutFailureHandling();
            if (status == Status.FAILURE) {
                NPCMovementComponent moveComponent = actor().getComponent(NPCMovementComponent.class);
                moveComponent.targetPosition = null;
                actor().save(moveComponent);
            }
            return status;
        }

        private Status updateWithoutFailureHandling() {
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
            Vector3f targetPosition = targetLocation.getWorldPosition();
            WalkableBlock targetBlock = pathfinderSystem.getBlock(targetPosition);
            if (targetBlock == null) {
                return Status.FAILURE;
            }

            if (nextPath != null) {
                calculatingPath = false;
                if (nextPath == Path.INVALID) {
                    return Status.FAILURE;
                }
                path = nextPath;
                nextPath = null;
                currentIndex = 0;
                setTargetBasedOnPathIndex();
            }

            if ((path == null || !path.getTarget().getBlockPosition().equals(targetBlock.getBlockPosition()))
                    && !calculatingPath) {
                MinionMoveComponent minionMoveComponent = actor().getComponent(MinionMoveComponent.class);
                WalkableBlock currentBlock = minionMoveComponent.currentBlock;
                if (currentBlock == null) {
                    return Status.FAILURE;
                }
                requestNextPath(targetBlock, currentBlock);
                calculatingPath = true;
            }
            if (path == null) {
                return Status.RUNNING;
            }

            if (currentIndex < path.size() && atSubTarget()) {
                currentIndex++;
                setTargetBasedOnPathIndex();
            }
            return Status.RUNNING;
        }

        private void requestNextPath(WalkableBlock targetBlock, WalkableBlock currentBlock) {
            pathfinderSystem.requestPath(
                    actor().getEntity(), currentBlock.getBlockPosition(),
                    Arrays.asList(targetBlock.getBlockPosition()), new PathfinderSystem.PathReadyCallback() {
                        @Override
                        public void pathReady(int pathId, List<Path> path, WalkableBlock target, List<WalkableBlock> start) {

                            if (path == null) {
                                nextPath = Path.INVALID;
                            } else if (path.size() > 0) {
                                nextPath = path.get(0);
                            }
                        }
                    });
        }

        private boolean atSubTarget() {
            LocationComponent location = actor().getComponent(LocationComponent.class);
            Vector3f worldPos = new Vector3f(location.getWorldPosition());
            Vector3f targetDelta = new Vector3f();
            targetDelta.sub(getSubTarget(), worldPos);
            float minDistance = 0.1f;
            return (targetDelta.x * targetDelta.x + targetDelta.z * targetDelta.z < minDistance * minDistance);
        }

        private void setTargetBasedOnPathIndex() {
            NPCMovementComponent moveComponent = actor().getComponent(NPCMovementComponent.class);
            if (currentIndex < path.size()) {
                moveComponent.targetPosition = getSubTarget();
                LocationComponent location = actor().getComponent(LocationComponent.class);
                CharacterMoveInputEvent inputEvent = null;
                Vector3f worldPos = new Vector3f(location.getWorldPosition());
                Vector3f targetDirection = new Vector3f();
                targetDirection.sub(moveComponent.targetPosition, worldPos);
                float yaw = (float) Math.atan2(targetDirection.x, targetDirection.z);
                moveComponent.yaw = 180f +  yaw * TeraMath.RAD_TO_DEG;
            }else {
                moveComponent.targetPosition = null;
            }
            actor().save(moveComponent);
        }

        /**
         * Calculates the rotation around y axis according to a formula from
         * https://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles
         */
        private float calculateYAxisRotation(Quat4f q) {
            return (float) Math.asin(2*(q.getX()*q.getZ() + q.getW() *q.getY()));
        }

        private Vector3f getSubTarget() {
            WalkableBlock subTargetBlock = path.get(currentIndex);
            Vector3f subTargetPosition = subTargetBlock.getBlockPosition().toVector3f();
            subTargetPosition.add(new Vector3f(0, 1, 0));
            return subTargetPosition;
        }

        @Override
        public FollowNode getNode () {
            return (FollowNode) super.getNode();
        }

        @Override
        public void handle(Status result) {

        }

        @Override
        public void onTerminate(Status result) {
            NPCMovementComponent moveComponent = actor().getComponent(NPCMovementComponent.class);
            moveComponent.targetPosition = null;
            actor().save(moveComponent);
        }
    }



}
