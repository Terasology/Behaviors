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

import org.joml.Vector3f;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.characters.CharacterMoveInputEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.minion.move.MinionMoveComponent;

@BehaviorAction(name = "stop_moving")
public class StopMovingAction extends BaseAction {

    @Override
    public void construct(Actor actor) {

        // Calculating a lot of superfluous stuff to debug; this'll get cleaned up when stopping is figured out
        LocationComponent locationComponent = actor.getComponent(LocationComponent.class);
        MinionMoveComponent moveComponent = actor.getComponent(MinionMoveComponent.class);
        Vector3f worldPos = locationComponent.getWorldPosition(new Vector3f());
        Vector3f targetDirection = moveComponent.target.sub(worldPos, new Vector3f());

        float yaw = (float) Math.atan2(targetDirection.x, targetDirection.z);

        CharacterMoveInputEvent wantedInput = new CharacterMoveInputEvent(0, 0, 0, new Vector3f(), false, false, false, (long) (actor.getDelta() * 1000));
        actor.getEntity().send(wantedInput);
    }
}
