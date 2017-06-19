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

import com.google.common.collect.Lists;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.behavior.tree.Node;
import org.terasology.logic.behavior.tree.Status;
import org.terasology.logic.behavior.tree.Task;
import org.terasology.logic.characters.AliveCharacterComponent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.network.ClientComponent;
import org.terasology.minion.components.FollowComponent;
import org.terasology.registry.In;
import org.terasology.rendering.nui.properties.Range;

import java.util.List;

/**
 * Makes the character follow a player within a given range
 */
public class FollowPlayerWithinRangeNode extends Node {

    @Range(min = 2, max = 50)
    private float maxDistance = 15.0f;

    @Override
    public FollowPlayerWithinRangeTask createTask() {
        return new FollowPlayerWithinRangeTask(this);
    }

    public static class FollowPlayerWithinRangeTask extends Task {

        @In
        private EntityManager entityManager;

        public FollowPlayerWithinRangeTask(Node node) {
            super(node);
        }

        @Override
        public Status update(float dt) {
            LocationComponent actorLocationComponent = actor().getComponent(LocationComponent.class);
            if (actorLocationComponent == null) {
                return Status.FAILURE;
            }
            Vector3f actorPosition = actorLocationComponent.getWorldPosition();

            float maxDistanceSquared = getNode().getMaxDistance()*getNode().getMaxDistance();
            Iterable<EntityRef> clients = entityManager.getEntitiesWith(ClientComponent.class);
            List<EntityRef> charactersWithinRange = Lists.newArrayList();
            for (EntityRef client: clients) {
                ClientComponent clientComponent = client.getComponent(ClientComponent.class);
                EntityRef character = clientComponent.character;
                AliveCharacterComponent aliveCharacterComponent = character.getComponent(AliveCharacterComponent.class);
                if (aliveCharacterComponent == null) {
                    continue;
                }
                LocationComponent locationComponent = character.getComponent(LocationComponent.class);
                if (locationComponent == null) {
                    continue;
                }
                if (locationComponent.getWorldPosition().distanceSquared(actorPosition) <= maxDistanceSquared) {
                    charactersWithinRange.add(character);
                }
            }

            if (charactersWithinRange.isEmpty()) {
                return Status.FAILURE;
            }

            FollowComponent followWish = actor().getComponent(FollowComponent.class);
            if (followWish == null) {
                return Status.FAILURE;
            }
            // TODO select closest character
            EntityRef someCharacterWithinRange = charactersWithinRange.get(0);
            followWish.entityToFollow = someCharacterWithinRange;
            actor().save(followWish);
            return Status.SUCCESS;
        }

        @Override
        public void handle(Status result) {

        }

        @Override
        public FollowPlayerWithinRangeNode getNode() {
            return (FollowPlayerWithinRangeNode) super.getNode();
        }
    }

    public float getMaxDistance() {
        return maxDistance;
    }
}
