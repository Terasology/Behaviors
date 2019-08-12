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

import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.logic.health.event.DoDamageEvent;
import org.terasology.logic.health.EngineDamageTypes;
import org.terasology.logic.health.HealthComponent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.behaviors.components.FollowComponent;
import org.terasology.registry.In;
import org.terasology.rendering.nui.properties.Range;

@BehaviorAction(name = "damage_followed_entity")
public class DamageFollowedEntityAction extends BaseAction {

    @Range(min = 0, max = 40)
    private int damage = 5;

    @Range(min = 0, max = 10)
    private int attackRange = 4;

    @In
    private EntityManager entityManager;

    @Override
    public BehaviorState modify(Actor actor, BehaviorState state) {
        FollowComponent followComponent = actor.getComponent(FollowComponent.class);
        if (followComponent == null) {
            return BehaviorState.FAILURE;
        }
        EntityRef entityToAttack = followComponent.entityToFollow;
        HealthComponent healthComponent = entityToAttack.getComponent(HealthComponent.class);
        if (healthComponent == null) {
            return BehaviorState.FAILURE;
        }
        if (actor.getComponent(LocationComponent.class).getWorldPosition().distance(entityToAttack.getComponent(LocationComponent.class).getWorldPosition()) > attackRange) {
            return BehaviorState.FAILURE;
        }

        Prefab damageType = EngineDamageTypes.PHYSICAL.get();
        entityToAttack.send(new DoDamageEvent(damage, damageType));
        return BehaviorState.SUCCESS;
    }

}
