// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.actions;

import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.logic.characters.CharacterMovementComponent;
import org.terasology.nui.properties.Range;

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
