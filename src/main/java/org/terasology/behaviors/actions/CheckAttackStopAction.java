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

import org.terasology.behaviors.components.AttackInProximityComponent;
import org.terasology.behaviors.components.AttackOnHitComponent;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.behaviors.components.FollowComponent;
import org.terasology.rendering.nui.properties.Range;


@BehaviorAction(name = "check_attack_stop")
public class CheckAttackStopAction extends BaseAction {

    @Range(max = 40)
    private float maxDistance = 10f;

    /**
     * Makes the character follow a player within a given range
     * Sends FAILURE when the distance is greater than maxDistance
     */
    @Override
    public BehaviorState modify(Actor actor, BehaviorState state) {
        BehaviorState status = getBehaviorStateWithoutReturn(actor);
        if (status == BehaviorState.FAILURE) {
            if (actor.hasComponent(AttackOnHitComponent.class)) {
                AttackOnHitComponent attackOnHitComponent = actor.getComponent(AttackOnHitComponent.class);
                attackOnHitComponent.instigator = null;
                actor.getEntity().saveComponent(attackOnHitComponent);
            } else if (actor.hasComponent(AttackInProximityComponent.class)) {
                AttackInProximityComponent attackInProximityComponent = actor.getComponent(AttackInProximityComponent.class);
                attackInProximityComponent.instigator = null;
                actor.getEntity().saveComponent(attackInProximityComponent);
            }
            actor.getEntity().removeComponent(FollowComponent.class);
        }
        return status;
    }

    private BehaviorState getBehaviorStateWithoutReturn(Actor actor) {
        LocationComponent actorLocationComponent = actor.getComponent(LocationComponent.class);
        if (actorLocationComponent == null) {
            return BehaviorState.FAILURE;
        }
        Vector3f actorPosition = actorLocationComponent.getWorldPosition();
        float maxDistance = this.maxDistance;
        if (actor.hasComponent(AttackOnHitComponent.class)) {
            maxDistance = actor.getComponent(AttackOnHitComponent.class).maxDistance;
        } else if (actor.hasComponent(AttackInProximityComponent.class)) {
            maxDistance = actor.getComponent(AttackInProximityComponent.class).maxDistance;
        }

        float maxDistanceSquared = maxDistance * maxDistance;
        FollowComponent followWish = actor.getComponent(FollowComponent.class);
        if (followWish == null || followWish.entityToFollow == null) {
            return BehaviorState.FAILURE;
        }

        LocationComponent locationComponent = followWish.entityToFollow.getComponent(LocationComponent.class);
        if (locationComponent == null) {
            return BehaviorState.FAILURE;
        }
        if (locationComponent.getWorldPosition().distanceSquared(actorPosition) <= maxDistanceSquared) {
            return BehaviorState.SUCCESS;
        }
        return BehaviorState.FAILURE;
    }

}
