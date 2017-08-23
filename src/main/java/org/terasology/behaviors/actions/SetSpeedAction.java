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

import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.logic.characters.CharacterMovementComponent;
import org.terasology.rendering.nui.properties.Range;

/*
 * Sets the speed multiplier of the entity
 * to the multiplier specified in the parameter.
 */
@BehaviorAction(name = "set_speed")
public class SetSpeedAction extends BaseAction {

    @Range(max = 10f)
    private float speedMultiplier;

    @Override
    public void construct(Actor actor) {
        CharacterMovementComponent characterMovementComponent = actor.getComponent(CharacterMovementComponent.class);
        characterMovementComponent.speedMultiplier = speedMultiplier;
        actor.getEntity().saveComponent(characterMovementComponent);
    }

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        return BehaviorState.SUCCESS;
    }
}
