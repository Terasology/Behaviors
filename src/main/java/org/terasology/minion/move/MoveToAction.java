// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion.move;

import org.joml.Math;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.logic.characters.CharacterMoveInputEvent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.nui.properties.Range;

import static org.joml.Math.abs;

/**
 * <b>Properties:</b> <b>distance</b><br/>
 * <br/> Moves the actor to the target defined by <b>MinionMoveComponent</b>.<br/> <br/>
 * <b>SUCCESS</b>: when distance between actor and target is below <b>distance</b>.<br/>
 * <b>FAILURE</b>: when there is no target.<br/>
 * <br/> Auto generated javadoc - modify README.markdown instead!
 */
@BehaviorAction(name = "move_to")
public class MoveToAction extends BaseAction {
    private static Logger logger = LoggerFactory.getLogger(MoveToAction.class);
    @Range(min = 0, max = 10)
    private float distance = 0.2f;


    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        BehaviorState state = BehaviorState.FAILURE;
        MinionMoveComponent moveComponent = actor.getComponent(MinionMoveComponent.class);

        if (moveComponent.target == null) {
            return BehaviorState.FAILURE;
        }
        if (moveComponent.type == MinionMoveComponent.Type.DIRECT) {

            boolean reachedTarget = processDirect(actor, moveComponent);
            state = reachedTarget ? BehaviorState.SUCCESS : BehaviorState.RUNNING;

        }
        if (moveComponent != null && moveComponent.target != null) {
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
        Vector3f worldPos = locationComponent.getWorldPosition(new Vector3f());
        Vector3f targetDirection = moveComponent.target.sub(worldPos, new Vector3f());
        Vector3f drive = new Vector3f();
        float yaw = (float) Math.atan2(targetDirection.x, targetDirection.z);
        float requestedYaw = (float) (180f + Math.toDegrees(yaw));

        if (abs(targetDirection.x) < distance && (abs(targetDirection.y) < 2f) && (abs(targetDirection.z) < distance)) {
            drive.set(0, 0, 0);
            reachedTarget = true;
        } else {
            targetDirection.normalize();
            drive.set(targetDirection);
        }

        CharacterMoveInputEvent wantedInput = new CharacterMoveInputEvent(0, 0, requestedYaw,
            drive, false, false,
            moveComponent.jumpMode, (long) (actor.getDelta() * 1000));
        actor.getEntity().send(wantedInput);


        return reachedTarget;
    }

}
