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
