// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.actions;

import org.joml.Vector3f;
import org.terasology.module.behaviors.components.TargetComponent;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.nui.properties.Range;

/**
 * Checks whether the entity should continue targeting its current target (as defined by the {@link TargetComponent}).
 * <br/>
 * <b>SUCCESS</b>: when the actor is within {@code maxDistance} squared of the target.<br/>
 * <b>FAILURE</b>: when there is no target, the target does not have a {@link LocationComponent},
 * or the target is too far away.<br/>
 */
@BehaviorAction(name = "check_target_stop")
public class CheckTargetStopAction extends BaseAction {

    /**
     * The maximum distance that the entity will continue targeting its target. Once the target moves <em>greater</em>
     * than this distance away, the entity will stop targeting it
     */
    @Range(max = 40)
    private float maxDistance = 10f;

    /**
     * Makes the character actively target a player within a given range
     * Sends FAILURE when the distance is greater than maxDistance.
     * What the character <em>does</em> with/to the target is determined by other behaviors
     */
    @Override
    public BehaviorState modify(Actor actor, BehaviorState state) {
        return getBehaviorState(actor);
    }

    private BehaviorState getBehaviorState(Actor actor) {
        LocationComponent actorLocationComponent = actor.getComponent(LocationComponent.class);
        if (actorLocationComponent == null) {
            return BehaviorState.FAILURE;
        }
        Vector3f actorPosition = actorLocationComponent.getWorldPosition(new Vector3f());

        float maxDistanceSquared = this.maxDistance * this.maxDistance;
        TargetComponent targetWish = actor.getComponent(TargetComponent.class);

        return processTarget(targetWish, actorPosition, maxDistanceSquared);

    }

    private BehaviorState processTarget(TargetComponent targetComponent,
                                        Vector3f actorPosition,
                                        float maxDistanceSquared) {
        if (targetComponent == null || targetComponent.target == null) {
            return BehaviorState.FAILURE;
        }

        LocationComponent locationComponent = targetComponent.target.getComponent(LocationComponent.class);
        if (locationComponent == null) {
            return BehaviorState.FAILURE;
        }
        if (locationComponent.getWorldPosition(new Vector3f()).distanceSquared(actorPosition) <= maxDistanceSquared) {
            return BehaviorState.SUCCESS;
        }
        return BehaviorState.FAILURE;
    }
}
