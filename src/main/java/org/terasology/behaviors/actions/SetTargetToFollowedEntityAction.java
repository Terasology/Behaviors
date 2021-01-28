// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.actions;

import org.joml.Vector3f;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.logic.location.LocationComponent;
import org.terasology.behaviors.components.FollowComponent;
import org.terasology.minion.move.MinionMoveComponent;

@BehaviorAction(name = "set_target_to_followed_entity")
public class SetTargetToFollowedEntityAction extends BaseAction {

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {

        MinionMoveComponent moveComponent = actor.getComponent(MinionMoveComponent.class);
        if (moveComponent.currentBlock != null) {
            EntityRef followedEntity = actor.getComponent(FollowComponent.class).entityToFollow;
            if (followedEntity != null && followedEntity != EntityRef.NULL) {
                moveComponent.target = followedEntity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f());
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

