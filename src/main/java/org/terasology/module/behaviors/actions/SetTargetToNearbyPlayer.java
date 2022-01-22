// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.actions;

import org.terasology.module.behaviors.components.AttackOnHitComponent;
import org.terasology.module.behaviors.components.FindNearbyPlayersComponent;
import org.terasology.module.behaviors.components.FollowComponent;
import org.terasology.engine.core.Time;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.registry.In;

@BehaviorAction(name = "set_target_nearby_player")
public class SetTargetToNearbyPlayer extends BaseAction {
    @In
    private Time time;

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        if (!actor.hasComponent(AttackOnHitComponent.class) || !actor.hasComponent(FindNearbyPlayersComponent.class)) {
            return BehaviorState.FAILURE;
        }

        AttackOnHitComponent attackOnHitComponent = actor.getComponent(AttackOnHitComponent.class);
        attackOnHitComponent.instigator = actor.getComponent(FindNearbyPlayersComponent.class).closestCharacter;
        attackOnHitComponent.timeWhenHit = time.getGameTimeInMs();
        actor.save(attackOnHitComponent);

        FollowComponent followComponent = new FollowComponent();
        followComponent.entityToFollow = attackOnHitComponent.instigator;
        actor.getEntity().addOrSaveComponent(followComponent);

        return BehaviorState.SUCCESS;
    }
}