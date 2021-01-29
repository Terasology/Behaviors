// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.actions;

import org.terasology.behaviors.components.FindNearbyPlayersComponent;
import org.terasology.behaviors.components.FollowComponent;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;


@BehaviorAction(name = "followCharacter")
public class FollowCharacterAction extends BaseAction {

    @Override
    public void construct(Actor actor) {
        FollowComponent followComponent = new FollowComponent();
        FindNearbyPlayersComponent component = actor.getComponent(FindNearbyPlayersComponent.class);
        followComponent.entityToFollow = component.closestCharacter;
        actor.save(followComponent);

    }

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        return BehaviorState.SUCCESS;
    }
}

