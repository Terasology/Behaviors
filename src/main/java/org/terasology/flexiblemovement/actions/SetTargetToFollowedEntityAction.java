// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblemovement.actions;

import org.terasology.behaviors.components.FollowComponent;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.flexiblemovement.FlexibleMovementComponent;

@BehaviorAction(name = "flex_set_target_to_followed_entity")
public class SetTargetToFollowedEntityAction extends BaseAction {

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {

        FlexibleMovementComponent moveComponent = actor.getComponent(FlexibleMovementComponent.class);
        if (moveComponent != null) {
            EntityRef followedEntity = actor.getComponent(FollowComponent.class).entityToFollow;
            if (followedEntity != null && followedEntity != EntityRef.NULL) {
                moveComponent.setPathGoal(followedEntity);
                actor.save(moveComponent);
            } else {
                return BehaviorState.FAILURE;
            }

        } else {
            return BehaviorState.FAILURE;
        }
        return BehaviorState.SUCCESS;

    }
}

