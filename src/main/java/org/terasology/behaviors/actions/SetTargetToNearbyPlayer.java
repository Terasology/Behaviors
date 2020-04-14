/*
 * Copyright 2020 MovingBlocks
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

import org.terasology.behaviors.components.AttackOnHitComponent;
import org.terasology.behaviors.components.FindNearbyPlayersComponent;
import org.terasology.behaviors.components.FollowComponent;
import org.terasology.engine.Time;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.registry.In;

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
