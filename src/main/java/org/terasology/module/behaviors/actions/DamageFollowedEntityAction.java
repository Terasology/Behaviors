// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.actions;

import org.joml.Vector3f;
import org.terasology.module.behaviors.components.FollowComponent;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.logic.health.EngineDamageTypes;
import org.terasology.module.health.components.HealthComponent;
import org.terasology.module.health.events.DoDamageEvent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.In;
import org.terasology.nui.properties.Range;

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
        Vector3f actorPos = actor.getComponent(LocationComponent.class).getWorldPosition(new Vector3f());
        Vector3f targetPos = entityToAttack.getComponent(LocationComponent.class).getWorldPosition(new Vector3f());
        if (actorPos.distance(targetPos) > attackRange) {
            return BehaviorState.FAILURE;
        }

        Prefab damageType = EngineDamageTypes.PHYSICAL.get();
        entityToAttack.send(new DoDamageEvent(damage, damageType, actor.getEntity()));
        return BehaviorState.SUCCESS;
    }

}
