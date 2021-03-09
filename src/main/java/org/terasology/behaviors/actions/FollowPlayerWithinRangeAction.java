// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.actions;

import com.google.common.collect.Lists;
import org.joml.Vector3f;
import org.terasology.behaviors.components.FollowComponent;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.logic.characters.AliveCharacterComponent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.network.ClientComponent;
import org.terasology.engine.registry.In;
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
        Vector3f actorPosition = actorLocationComponent.getWorldPosition(new Vector3f());

        float maxDistanceSquared = (float) Math.pow(maxDistance, 2);
        Iterable<EntityRef> clients = entityManager.getEntitiesWith(ClientComponent.class);
        List<EntityRef> charactersWithinRange = Lists.newArrayList();

        EntityRef closestCharacter = EntityRef.NULL;
        float minDistanceFromCharacter = 0.0f;

        Vector3f locationPosition = new Vector3f();
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
            locationComponent.getWorldPosition(locationPosition);
            if (locationPosition.distanceSquared(actorPosition) <= maxDistanceSquared) {
                if (charactersWithinRange.size() == 0) {
                    closestCharacter = character;
                    minDistanceFromCharacter = locationPosition.distanceSquared(actorPosition);
                } else {
                    if (locationPosition.distanceSquared(actorPosition) < minDistanceFromCharacter) {
                        closestCharacter = character;
                        minDistanceFromCharacter = locationPosition.distanceSquared(actorPosition);
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
