// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.actions;

import org.terasology.behaviors.minion.move.MinionMoveComponent;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.characters.CharacterMoveInputEvent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Vector3f;

@BehaviorAction(name = "stop_moving")
public class StopMovingAction extends BaseAction {

    @Override
    public void construct(Actor actor) {

        // Calculating a lot of superfluous stuff to debug; this'll get cleaned up when stopping is figured out
        LocationComponent locationComponent = actor.getComponent(LocationComponent.class);
        MinionMoveComponent moveComponent = actor.getComponent(MinionMoveComponent.class);
        Vector3f worldPos = new Vector3f(locationComponent.getWorldPosition());
        Vector3f targetDirection = new Vector3f();
        targetDirection.sub(moveComponent.target, worldPos);

        float yaw = (float) Math.atan2(targetDirection.x, targetDirection.z);
        float requestedYaw = 180f + yaw * TeraMath.RAD_TO_DEG;

        CharacterMoveInputEvent wantedInput = new CharacterMoveInputEvent(0, 0, 0, Vector3f.zero(), false, false,
                false, (long) (actor.getDelta() * 1000));
        actor.getEntity().send(wantedInput);
    }
}
