// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.actions;

import org.terasology.behaviors.components.FindNearbyPlayersComponent;
import org.terasology.behaviors.components.FleeingComponent;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;

@BehaviorAction(name = "flee_from_character")
public class FleeFromCharacterAction extends BaseAction {
    @Override
    public void construct(Actor actor) {
        FleeingComponent fleeingComponent = new FleeingComponent();
        FindNearbyPlayersComponent component = actor.getComponent(FindNearbyPlayersComponent.class);
        fleeingComponent.instigator = component.closestCharacter;
        actor.save(fleeingComponent);

    }

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        return BehaviorState.SUCCESS;
    }
}
