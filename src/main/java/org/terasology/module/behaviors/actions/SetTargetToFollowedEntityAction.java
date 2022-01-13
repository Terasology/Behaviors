// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.actions;

import org.joml.Vector3f;
import org.terasology.module.behaviors.components.FollowComponent;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.module.behaviors.components.MinionMoveComponent;

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

