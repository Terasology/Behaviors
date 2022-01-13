// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.actions;

import org.terasology.module.behaviors.components.FindNearbyPlayersComponent;
import org.terasology.module.behaviors.components.TargetComponent;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;

/**
 * Sets the {@link TargetComponent} of the Actor to the nearest Player. The target can then be used for various NPC
 * actions. E.g. a guard NPC may target a player to shoot at them if they get too close, a medic NPC may target a
 * player to move towards and heal them.
 * <br/>
 * <b>Note:</b> this action should be guarded by a check on {@link FindNearbyPlayersComponent}. I.e.
 * <pre>
 *     guard: {
 *           componentPresent: "Behaviors:FindNearbyPlayers",
 *           values: ["N charactersWithinRange nonEmpty"],
 *           ...
 * </pre>
 */
@BehaviorAction(name = "target_character")
public class TargetCharacterAction extends BaseAction {

    @Override
    public void construct(Actor actor) {
        TargetComponent targetComponent = new TargetComponent();
        FindNearbyPlayersComponent component = actor.getComponent(FindNearbyPlayersComponent.class);
        targetComponent.target = component.closestCharacter;
        actor.save(targetComponent);
    }

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        return BehaviorState.SUCCESS;
    }
}

