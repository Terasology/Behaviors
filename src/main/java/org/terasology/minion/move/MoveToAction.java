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
 * Moves the actor to the target defined by {@link MinionMoveComponent#target}.
 * <br/>
 * Note: This action only moves the actor if its movement type is {@link MinionMoveComponent.Type.DIRECT}!
 * <br/>
 * The actor movement is based on the properties of {@link MinionMoveComponent#target}, in particular:
 *    - target                  The target position to move towards.
 *    - type                    The means how to determine the best path or action to reach the target (currently only DIRECT supported)
 *    - horizontalCollision     Whether the actor recently encountered a horizontal collision, e.g., walking into a wall
 *    - jumpCooldown            ??? set to 0.3 in case a horizontal collision is detected and reduced by Actor#getDelta() each evaluation. I think the entity will attempt to jump for 300ms after hitting an obstacle...
 *    - jumpMode                Whether the actor should jump on next movement.
 */
@BehaviorAction(name = "move_to")
public class MoveToAction extends BaseAction {
    private static Logger logger = LoggerFactory.getLogger(MoveToAction.class);

    /** To complete the "move" action the distance between the actor and the target needs to be below {@code distance}. */
    @Range(min = 0, max = 10)
    private float distance = 0.2f;

    /**
     * @return {@link BehaviorState.SUCCESS} when distance between actor and target is below {@code distance},
     *         {@link BehaviorState.FAILURE} when there is no target,
     *         {@link BehaviorState.RUNNING} while moving towards the target.
     */
    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        BehaviorState state = BehaviorState.FAILURE;

        MinionMoveComponent moveComponent = actor.getComponent(MinionMoveComponent.class);

        if (moveComponent.target == null) {
            return BehaviorState.FAILURE;
        }

        //TODO: also handle other movement types?
        if (moveComponent.type == MinionMoveComponent.Type.DIRECT) {
            boolean reachedTarget = processDirect(actor, moveComponent);
            state = reachedTarget ? BehaviorState.SUCCESS : BehaviorState.RUNNING;
        }
        //TODO: either make the null check before accessing the component, or just throw it out completely
        if (moveComponent != null && moveComponent.target != null) {
            // handle horizontal collisions, e.g., when the entity hits a wall
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

    /**
     * Move the actor's entity by sending a {@link CharacterMoveInputEvent} in the direction of the target defined by {@link MinionMoveComponent#target}.
     * <br/>
     * The actor has reached it's target if it is within {@code distance} along X and Z axis (horizontal), and within a distance of 2 along Y axis (vertical). 
     *
     * @param actor             The actor to be moved. Must have a {@link LocationComponent}.
     * @param moveComponent     The movement configuration for the actor.
     *
     * @return whether the actor reached the target (before the movement o.O)
     */
    private boolean processDirect(Actor actor, MinionMoveComponent moveComponent) {
        boolean reachedTarget = false;

        LocationComponent locationComponent = actor.getComponent(LocationComponent.class);
        Vector3f worldPos = locationComponent.getWorldPosition(new Vector3f());
        Vector3f targetDirection = moveComponent.target.sub(worldPos, new Vector3f());

        Vector3f drive = new Vector3f();
        float yaw = (float) Math.atan2(targetDirection.x, targetDirection.z);
        float requestedYaw = (float) (180f + Math.toDegrees(yaw));

        //TODO: why not do an early return when the target is reached without sending a CharacterMoveInputEvent?
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
