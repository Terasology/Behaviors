/*
 * Copyright 2020 MovingBlocks
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
import org.terasology.behaviors.components.TerritoryDistance;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.minion.move.MinionMoveComponent;

@BehaviorAction(name = "set_target_territory")
public class SetTargetToTerritory extends BaseAction {
    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        if (!actor.hasComponent(TerritoryDistance.class) || !actor.hasComponent(MinionMoveComponent.class)) {
            return BehaviorState.FAILURE;
        }

        Vector3f territory = actor.getComponent(TerritoryDistance.class).location;

        MinionMoveComponent moveComponent = actor.getComponent(MinionMoveComponent.class);
        if (moveComponent.target.equals(territory)) {
            return BehaviorState.SUCCESS;
        }

        moveComponent.target = territory;
        actor.save(moveComponent);

        return BehaviorState.SUCCESS;
    }
}
