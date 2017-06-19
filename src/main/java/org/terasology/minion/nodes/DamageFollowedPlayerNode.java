/*
 * Copyright 2016 MovingBlocks
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
package org.terasology.minion.nodes;

import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.logic.behavior.tree.Node;
import org.terasology.logic.behavior.tree.Status;
import org.terasology.logic.behavior.tree.Task;
import org.terasology.logic.health.DoDamageEvent;
import org.terasology.logic.health.EngineDamageTypes;
import org.terasology.logic.health.HealthComponent;
import org.terasology.minion.components.FollowComponent;
import org.terasology.registry.In;
import org.terasology.rendering.nui.properties.Range;

/**
 * Makes the character being followed take damage
 */
public class DamageFollowedPlayerNode extends Node {

    @Range(min = 0, max = 40)
    private int damage = 5;

    @Override
    public DamageFollowedPlayerTask createTask() {
        return new DamageFollowedPlayerTask(this);
    }

    public static class DamageFollowedPlayerTask extends Task {

        @In
        private EntityManager entityManager;

        public DamageFollowedPlayerTask(Node node) {
            super(node);
        }

        @Override
        public Status update(float dt) {
            FollowComponent followComponent = actor().getComponent(FollowComponent.class);
            if (followComponent == null) {
                return Status.FAILURE;
            }
            EntityRef entityToAttack = followComponent.entityToFollow;
            HealthComponent healthComponent = entityToAttack.getComponent(HealthComponent.class);
            if (healthComponent == null) {
                return Status.FAILURE;
            }
            Prefab damageType = EngineDamageTypes.PHYSICAL.get();
            entityToAttack.send(new DoDamageEvent(getNode().getDamage(), damageType));
            return Status.SUCCESS;
        }

        @Override
        public void handle(Status result) {

        }

        @Override
        public DamageFollowedPlayerNode getNode() {
            return (DamageFollowedPlayerNode) super.getNode();
        }
    }

    public int getDamage() {
        return damage;
    }
}
