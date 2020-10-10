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

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.GroupMindComponent;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.logic.characters.CharacterMovementComponent;
import org.terasology.nui.properties.Range;

@BehaviorAction(name = "set_flock_speed")
public class SetFlockSpeedAction extends BaseAction {

    @Range(max = 10f)
    private float speedMultiplier;

    @Override
    public void construct(Actor actor) {
        if (actor.hasComponent(GroupMindComponent.class)) {
            GroupMindComponent hivemindComponent = actor.getComponent(GroupMindComponent.class);

            if (!hivemindComponent.groupMembers.isEmpty()) {
                for (EntityRef entityRef : hivemindComponent.groupMembers) {
                    CharacterMovementComponent characterMovementComponent = entityRef.getComponent(CharacterMovementComponent.class);
                    characterMovementComponent.speedMultiplier = speedMultiplier;
                    entityRef.saveComponent(characterMovementComponent);
                }
            }
        }
    }

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        return BehaviorState.SUCCESS;
    }
}
