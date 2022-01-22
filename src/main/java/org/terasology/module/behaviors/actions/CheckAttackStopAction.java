// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.actions;

import org.joml.Vector3f;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.module.behaviors.components.AttackInProximityComponent;
import org.terasology.module.behaviors.components.AttackOnHitComponent;
import org.terasology.module.behaviors.components.FollowComponent;
import org.terasology.nui.properties.Range;

// a complex action replacing a behavior tree - is that a good idea or not?
// should this rather be:
//
// StopAttackIfOutOfFollowDistance.behavior
// selector [ // condition && clear
//    sequence: [ // AND
//        guard:
//            componentPresent: "LocationComponent"
//        guard:
//            componentPresent: "FollowComponent"
//            values: [ "N entityToFollow exists" ]
//        selector: [ // OR
//            guard:
//                componentPresent: "AttackOnHitComponent"
//            guard:
//                componentPresent: "AttackInProximityComponent"
//        ]
//        check_attack_target_in_reach
//    ]
//
//    // if failed
//    set_attack_target_clear
// ]
//
// or should this rather be handled by pure ECS logic?
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
        if (isTargetEntityOutOfFollowDistance(actor)) {
            clearAttackTarget(actor);
            return BehaviorState.FAILURE;
        }
        return BehaviorState.SUCCESS;
    }

    private void clearAttackTarget(Actor actor) {
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

    private boolean isTargetEntityOutOfFollowDistance(Actor actor) {
        LocationComponent actorLocationComponent = actor.getComponent(LocationComponent.class);
        if (actorLocationComponent == null) {
            return true;
        }
        Vector3f actorPosition = actorLocationComponent.getWorldPosition(new Vector3f());
        float distance = this.maxDistance;
        if (actor.hasComponent(AttackOnHitComponent.class)) {
            distance = actor.getComponent(AttackOnHitComponent.class).maxDistance;
        } else if (actor.hasComponent(AttackInProximityComponent.class)) {
            distance = actor.getComponent(AttackInProximityComponent.class).maxDistance;
        }

        float maxDistanceSquared = distance * distance;
        FollowComponent followWish = actor.getComponent(FollowComponent.class);
        if (followWish == null || followWish.entityToFollow == null) {
            return true;
        }

        LocationComponent locationComponent = followWish.entityToFollow.getComponent(LocationComponent.class);
        if (locationComponent == null) {
            return true;
        }
        if (locationComponent.getWorldPosition(new Vector3f()).distanceSquared(actorPosition) <= maxDistanceSquared) {
            return false;
        }
        return true;
    }

}
