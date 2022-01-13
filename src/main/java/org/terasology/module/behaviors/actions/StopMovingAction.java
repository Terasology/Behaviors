// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.actions;

import org.joml.Vector3f;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.characters.CharacterMoveInputEvent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.module.behaviors.components.MinionMoveComponent;

@BehaviorAction(name = "stop_moving")
public class StopMovingAction extends BaseAction {

    @Override
    public void construct(Actor actor) {

        // Calculating a lot of superfluous stuff to debug; this'll get cleaned up when stopping is figured out
        LocationComponent locationComponent = actor.getComponent(LocationComponent.class);
        MinionMoveComponent moveComponent = actor.getComponent(MinionMoveComponent.class);
        Vector3f worldPos = locationComponent.getWorldPosition(new Vector3f());
        Vector3f targetDirection = new Vector3f(moveComponent.target).sub(worldPos);

        float yaw = (float) Math.atan2(targetDirection.x, targetDirection.z);

        CharacterMoveInputEvent wantedInput = new CharacterMoveInputEvent(0, 0, yaw, new Vector3f(), false, false, false,
                (long) (actor.getDelta() * 1000));
        actor.getEntity().send(wantedInput);
    }
}
