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
                moveComponent.target = followedEntity.getComponent(LocationComponent.class).getWorldPosition();
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

