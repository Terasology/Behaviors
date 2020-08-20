// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.actions;

import com.google.common.collect.Lists;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.logic.characters.AliveCharacterComponent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.behaviors.components.FollowComponent;
import org.terasology.network.ClientComponent;
import org.terasology.registry.In;
import org.terasology.nui.properties.Range;

import java.util.List;

@BehaviorAction(name = "follow_player_within_range")
public class FollowPlayerWithinRangeAction extends BaseAction {

    @Range(min = 2, max = 50)
    private float maxDistance = 15.0f;

    @In
    private EntityManager entityManager;

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        LocationComponent actorLocationComponent = actor.getComponent(LocationComponent.class);
        if (actorLocationComponent == null) {
            return BehaviorState.FAILURE;
        }
        Vector3f actorPosition = actorLocationComponent.getWorldPosition();

        float maxDistanceSquared = (float) Math.pow(maxDistance, 2);
        Iterable<EntityRef> clients = entityManager.getEntitiesWith(ClientComponent.class);
        List<EntityRef> charactersWithinRange = Lists.newArrayList();

        EntityRef closestCharacter = EntityRef.NULL;
        float minDistanceFromCharacter = 0.0f;

        for (EntityRef client : clients) {
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
                if (charactersWithinRange.size() == 0) {
                    closestCharacter = character;
                    minDistanceFromCharacter = locationComponent.getWorldPosition().distanceSquared(actorPosition);
                } else {
                    if (locationComponent.getWorldPosition().distanceSquared(actorPosition) < minDistanceFromCharacter) {
                        closestCharacter = character;
                        minDistanceFromCharacter = locationComponent.getWorldPosition().distanceSquared(actorPosition);
                    }
                }

                charactersWithinRange.add(character);
            }
        }

        if (charactersWithinRange.isEmpty()) {
            return BehaviorState.FAILURE;
        }

        FollowComponent followWish = actor.getComponent(FollowComponent.class);
        if (followWish == null) {
            return BehaviorState.FAILURE;
        }

        followWish.entityToFollow = closestCharacter;
        actor.save(followWish);
        return BehaviorState.SUCCESS;
    }

}
