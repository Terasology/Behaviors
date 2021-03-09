// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.actions;

import org.joml.Vector3f;
import org.terasology.behaviors.components.FollowComponent;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.nui.properties.Range;

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
        Vector3f targetPoint = targetLocation.getWorldPosition(new Vector3f());

        LocationComponent currentLocation = actor.getComponent(LocationComponent.class);
        if (currentLocation == null) {
            return BehaviorState.FAILURE;
        }
        Vector3f currentPoint = currentLocation.getWorldPosition(new Vector3f());

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
