// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.actions;

import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.behaviors.components.FleeingComponent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.logic.location.LocationComponent;


@BehaviorAction(name = "check_flee_stop")
public class CheckFleeStopAction extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(CheckFleeStopAction.class);

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {

        FleeingComponent fleeingComponent = actor.getComponent(FleeingComponent.class);
        EntityRef instigator = fleeingComponent.instigator;
        if (instigator == null || !instigator.isActive()) {
            return BehaviorState.FAILURE;
        }
        LocationComponent targetLocation = instigator.getComponent(LocationComponent.class);
        if (targetLocation == null) {
            return BehaviorState.FAILURE;
        }
        LocationComponent currentLocation = actor.getComponent(LocationComponent.class);
        if (currentLocation == null) {
            return BehaviorState.FAILURE;
        }
        Vector3f instigatorLocation = currentLocation.getWorldPosition(new Vector3f());
        Vector3f selfLocation = targetLocation.getWorldPosition(new Vector3f());
        float currentDistanceSquared = selfLocation.distanceSquared(instigatorLocation);

        float minDistance = fleeingComponent.minDistance;
        float minDistanceSquared = minDistance * minDistance;

        if (currentDistanceSquared >= minDistanceSquared) {
            actor.getEntity().removeComponent(FleeingComponent.class);
            return BehaviorState.FAILURE;
        } else {
            return BehaviorState.SUCCESS;
        }
    }

}
