/*
 * Copyright 2019 MovingBlocks
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.behaviors.components.FindNearbyPlayersComponent;
import org.terasology.behaviors.components.TargetComponent;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;

/**
 * Similar to {@code FollowComponent} but more generic. E.g. we may want to track the player to shoot at them
 * but not follow them.
 */
@BehaviorAction(name = "target_character")
public class TargetCharacterAction extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(TargetCharacterAction.class);

    @Override
    public void construct(Actor actor) {
        TargetComponent targetComponent = new TargetComponent();
        FindNearbyPlayersComponent component = actor.getComponent(FindNearbyPlayersComponent.class);
        targetComponent.target = component.closestCharacter;
        actor.save(targetComponent);
        logger.trace("Setting target to {}", targetComponent.target.getParentPrefab().toString());
    }

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        return BehaviorState.SUCCESS;
    }
}

