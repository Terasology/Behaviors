/*
 * Copyright 2017 MovingBlocks
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
package org.terasology.behaviors.actions;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.behaviors.components.FollowComponent;
import org.terasology.rendering.nui.properties.Range;

@BehaviorAction(name = "continue_following_check")
public class ContinueFollowingCheckAction extends BaseAction {

    @Range(min = 0, max = 20)
    private float minDistance = 0.0f;

    @Range(min = 0, max = 100)
    private float maxDistance = 100.0f;

    private boolean reverse = false;

    @Override
    public BehaviorState modify(Actor actor, BehaviorState state) {
        FollowComponent followWish = actor.getComponent(FollowComponent.class);
        if (followWish == null) {
            return BehaviorState.FAILURE;
        }

        EntityRef entityToFollow = followWish.entityToFollow;
        if (entityToFollow == null || !entityToFollow.isActive()) {
            return BehaviorState.FAILURE;
        }
        LocationComponent targetLocation = entityToFollow.getComponent(LocationComponent.class);
        if (targetLocation == null) {
            return BehaviorState.FAILURE;
        }
        Vector3f targetPoint = targetLocation.getWorldPosition();

        LocationComponent currentLocation = actor.getComponent(LocationComponent.class);
        if (currentLocation == null) {
            return BehaviorState.FAILURE;
        }
        Vector3f currentPoint = currentLocation.getWorldPosition();

        float minDistanceSquared = minDistance * minDistance;
        float maxDistanceSquared = maxDistance * maxDistance;
        float currentDistanceSquared = currentPoint.distanceSquared(targetPoint);
        if (currentDistanceSquared <= minDistanceSquared) {
            return send(BehaviorState.FAILURE);
        }
        if (currentDistanceSquared >= maxDistanceSquared) {
            return send(BehaviorState.FAILURE);
        }

        return send(BehaviorState.SUCCESS);
    }

    private BehaviorState send(BehaviorState state) {
        if (reverse) {
            switch (state) {
                case FAILURE:
                    return BehaviorState.SUCCESS;
                case SUCCESS:
                    return BehaviorState.FAILURE;
            }
        }
        return state;

    }

}
