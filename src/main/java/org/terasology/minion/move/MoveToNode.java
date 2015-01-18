/*
 * Copyright 2015 MovingBlocks
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

import org.terasology.logic.behavior.ActionName;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.logic.characters.CharacterMoveInputEvent;
import org.terasology.logic.characters.CharacterMovementComponent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Vector3f;
import org.terasology.rendering.nui.properties.Range;

/**
 * <b>Properties:</b> <b>distance</b><br/>
 * <br/>
 * Moves the actor to the target defined by <b>MinionMoveComponent</b>.<br/>
 * <br/>
 * <b>SUCCESS</b>: when distance between actor and target is below <b>distance</b>.<br/>
 * <b>FAILURE</b>: when there is no target.<br/>
 * <br/>
 * Auto generated javadoc - modify README.markdown instead!
 */
@ActionName("move_to")
public class MoveToNode extends BaseAction {
    @Range(min = 0, max = 10)
    private float distance;


    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        BehaviorState state = BehaviorState.FAILURE;
        MinionMoveComponent moveComponent = actor.component(MinionMoveComponent.class);
        if (moveComponent != null && moveComponent.target != null) {
            if (moveComponent.horizontalCollision) {
                moveComponent.horizontalCollision = false;
                moveComponent.jumpCooldown = 0.3f;
            }
            moveComponent.jumpCooldown -= actor.getDelta();
            moveComponent.jumpMode = moveComponent.jumpCooldown > 0;
            actor.save(moveComponent);
            state = setMovement(actor, moveComponent);
        }
        return state;
    }

    private BehaviorState setMovement(Actor actor, MinionMoveComponent moveComponent) {
        BehaviorState result;
        LocationComponent location = actor.location();
        Vector3f worldPos = new Vector3f(location.getWorldPosition());
        Vector3f targetDirection = new Vector3f();
        targetDirection.sub(moveComponent.target, worldPos);
        Vector3f drive = new Vector3f();
        float yaw = (float) Math.atan2(targetDirection.x, targetDirection.z);

        result = BehaviorState.RUNNING;
        if (targetDirection.x * targetDirection.x + targetDirection.z * targetDirection.z > distance * distance) {
            targetDirection.scale(0.5f);
            drive.set(targetDirection);
        } else {
            drive.set(0, 0, 0);
            result = BehaviorState.SUCCESS;
        }

        float requestedYaw = 180f + yaw * TeraMath.RAD_TO_DEG;

        CharacterMoveInputEvent wantedInput = new CharacterMoveInputEvent(0, 0, requestedYaw, drive, false, moveComponent.jumpMode, (long) (actor.getDelta() * 1000));

        CharacterMovementComponent characterMovement = actor.minion().getComponent(CharacterMovementComponent.class);

        CharacterMoveInputEvent adjustedInput = calculateMovementInput(location, characterMovement, wantedInput, moveComponent.target);

        actor.minion().send(wantedInput);

        return result;
    }

    protected CharacterMoveInputEvent calculateMovementInput(LocationComponent location,
                                                             CharacterMovementComponent movementComp, CharacterMoveInputEvent input,
                                                             Vector3f currentTarget) {

        Vector3f moveDelta = input.getMovementDirection();
        float delta = input.getDelta();

        Vector3f term1 = new Vector3f(moveDelta);
        moveDelta.scale(1f / delta);

        Vector3f term2 = new Vector3f(term1);
        term2.sub(movementComp.getVelocity());

        Vector3f term3 = new Vector3f(term2);
        term3.scale(1f / Math.min(movementComp.mode.scaleInertia * delta, 1f));

        Vector3f term4 = new Vector3f(term3);
        term4.add(movementComp.getVelocity());

        Vector3f term5 = new Vector3f(term4);
        term5.scale(1f / movementComp.mode.maxSpeed);

        Vector3f desiredVelocity = term5;

        // Does not account for runFactor -- we are not currently supporting running
        // Does not account for removing y component while maintaining speed -- we are not currently setting a non-zero y component
        // Does not account for gravity's effect on the Y axis. -- this shouldn't affect horizontal travel.
        // Does not account for anything the physics engine might be doing

        CharacterMoveInputEvent newInput = new CharacterMoveInputEvent(input.getSequenceNumber(),
                input.getPitch(), input.getYaw(), desiredVelocity, input.isRunning(), input.isJumpRequested());

        return newInput;
    }


}
