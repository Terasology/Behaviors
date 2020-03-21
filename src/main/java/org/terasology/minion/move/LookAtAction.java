/*
 * Copyright 2019 MovingBlocks
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
package org.terasology.minion.move;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.behaviors.components.TargetComponent;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.logic.characters.CharacterMoveInputEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Vector3f;
import org.terasology.rendering.nui.properties.Range;

/**
 * Turns the actor to face the target defined by <b>TargetComponent</b>.<br/>
 * <br/>
 * <b>SUCCESS</b>: when the actor was turned in the direction of the target.<br/>
 * <b>FAILURE</b>: when there is no target or the target is already in sight.<br/>
 */
@BehaviorAction(name = "look_at")
public class LookAtAction extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(LookAtAction.class);

    /**
     * The maximum angle (in degrees) between the current view direction, and the view direction to the target, below
     * which the {@code Actor} will be considered "looking at" the target. I.e. the Actor will continue turning
     * toward the target until {@code Math.abs(requestedAngle - currentAngle) < maxAngleDegrees}
     */
    @Range(min = 0, max = 10)
    private float maxAngleDegrees = 2f;

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        TargetComponent targetComponent = actor.getComponent(TargetComponent.class);

        if (targetComponent.target == null) {
            return BehaviorState.FAILURE;
        }
        return process(actor, targetComponent);
    }

    private BehaviorState process(Actor actor, TargetComponent targetComponent) {

        LocationComponent locationComponent = actor.getComponent(LocationComponent.class);
        Vector3f worldPos = new Vector3f(locationComponent.getWorldPosition());
        Vector3f targetDirection = new Vector3f();
        LocationComponent targetLocation = targetComponent.target.getComponent(LocationComponent.class);
        targetDirection.sub(targetLocation.getWorldPosition(), worldPos);
        Vector3f drive = new Vector3f(); // Leave blank for no movement

        float yaw = (float) Math.atan2(targetDirection.x, targetDirection.z);
        float requestedYaw = 180f + yaw * TeraMath.RAD_TO_DEG;
        float currentYaw = locationComponent.getLocalRotation().getYaw() * TeraMath.RAD_TO_DEG;
        // Negative values should be wrapped around
        float correctedYaw = currentYaw < 0 ? currentYaw + 360f : currentYaw;

        // Is the angle between current and requested "close enough"
        boolean alreadyLooking = Math.abs(requestedYaw - correctedYaw) < maxAngleDegrees;

        if (alreadyLooking) {
            return BehaviorState.FAILURE;
        }

        targetDirection.normalize();

        CharacterMoveInputEvent wantedInput = new CharacterMoveInputEvent(
                0,
                0,
                requestedYaw,
                drive,
                false,
                false,
                false,
                (long) (actor.getDelta() * 1000));
        actor.getEntity().send(wantedInput);

        // TODO Some kind of ray cast to see if there are any obstacles
        return BehaviorState.SUCCESS;
    }

}
