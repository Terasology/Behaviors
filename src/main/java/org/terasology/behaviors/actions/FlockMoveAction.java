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
package org.terasology.behaviors.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.logic.characters.CharacterMoveInputEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Vector3f;
import org.terasology.minion.move.MinionMoveComponent;
import org.terasology.minion.move.MoveToAction;
import org.terasology.nui.properties.Range;
import org.terasology.behaviors.components.FlockComponent;

@BehaviorAction(name = "flock_move")
public class FlockMoveAction extends BaseAction {
    private static Logger logger = LoggerFactory.getLogger(MoveToAction.class);
    @Range(min = 0, max = 10)
    private float distance = 0.2f;


    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        BehaviorState state = BehaviorState.FAILURE;
        MinionMoveComponent moveComponent = actor.getComponent(MinionMoveComponent.class);
        FlockComponent flockComponent = actor.getComponent(FlockComponent.class);


        if ((null != moveComponent) && (null != flockComponent)) {
            if (flockComponent.flockCentre == null) {
                return BehaviorState.FAILURE;
            }

            moveComponent.target = flockComponent.flockCentre;

            if (moveComponent.type == MinionMoveComponent.Type.DIRECT) {

                boolean reachedTarget = processDirect(actor, moveComponent);
                state = reachedTarget ? BehaviorState.SUCCESS : BehaviorState.RUNNING;

            }

            if (moveComponent.horizontalCollision) {
                moveComponent.horizontalCollision = false;
                moveComponent.jumpCooldown = 0.3f;
            }
            moveComponent.jumpCooldown -= actor.getDelta();
            moveComponent.jumpMode = moveComponent.jumpCooldown > 0;
            actor.save(moveComponent);

        }

        return state;
    }

    private boolean processDirect(Actor actor, MinionMoveComponent moveComponent) {

        LocationComponent locationComponent = actor.getComponent(LocationComponent.class);
        boolean reachedTarget = false;
        Vector3f worldPos = new Vector3f(locationComponent.getWorldPosition());
        Vector3f targetDirection = new Vector3f();
        targetDirection.sub(moveComponent.target, worldPos);
        Vector3f drive = new Vector3f();

        float yaw = (float) Math.atan2(targetDirection.x, targetDirection.z);
        if((targetDirection.x < distance) && (targetDirection.y < distance) && (targetDirection.z < distance)) {

        //if (targetDirection.x * targetDirection.x + targetDirection.z * targetDirection.z <= distance * distance) {
            drive.set(0, 0, 0);
            reachedTarget = true;
        } else {
            targetDirection.normalize();
            drive.set(targetDirection);
        }
        float requestedYaw = 180f + yaw * TeraMath.RAD_TO_DEG;

        CharacterMoveInputEvent wantedInput = new CharacterMoveInputEvent(0, 0, requestedYaw, drive, false, false, moveComponent.jumpMode, (long) (actor.getDelta() * 1000));
        actor.getEntity().send(wantedInput);


        return reachedTarget;
    }
}
