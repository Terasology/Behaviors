// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.system;

import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.behaviors.components.FindNearbyPlayersComponent;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.engine.logic.characters.AliveCharacterComponent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.network.ClientComponent;
import org.terasology.engine.registry.In;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Update all {@link FindNearbyPlayersComponent}s on active entities with information about alive players within range and which of them is closest to the entity.
 * <br>
 * The update frequency is once per game tick.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class FindNearbyPlayersSystem extends BaseComponentSystem implements UpdateSubscriberSystem {

    private static final Logger logger = LoggerFactory.getLogger(FindNearbyPlayersSystem.class);

    @In
    private EntityManager entityManager;

    @Override
    public void update(float delta) {
        //TODO: Update with lower frequency.
        //      Updating the nearby entities every 200ms should be enough from a gameplay perspective and we can safe some
        //      computing cycles every tick that way.
        
        Map<Vector3f, EntityRef> clientLocationMap = new HashMap<>();

        for (EntityRef client : entityManager.getEntitiesWith(ClientComponent.class)) {
            ClientComponent clientComponent = client.getComponent(ClientComponent.class);
            EntityRef character = clientComponent.character;
            if (!character.hasComponent(AliveCharacterComponent.class)) {
                continue;
            }
            LocationComponent locationComponent = character.getComponent(LocationComponent.class);
            if (locationComponent == null) {
                continue;
            }

            clientLocationMap.put(locationComponent.getWorldPosition(new Vector3f()), character);
        }
        Set<Vector3f> locationSet = clientLocationMap.keySet();

        for (EntityRef entity : entityManager.getEntitiesWith(FindNearbyPlayersComponent.class)) {
            Vector3f actorPosition = entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f());
            FindNearbyPlayersComponent findNearbyPlayersComponent = entity.getComponent(FindNearbyPlayersComponent.class);
            float maxDistance = findNearbyPlayersComponent.searchRadius;
            float maxDistanceSquared = maxDistance * maxDistance;

            //TODO: Use a (shortest) path distance to determine "nearest" player instead of beeline distance.
            //      At least make it configurable via the FindNearbyPlayersComponent whether to use path or beeline.
            List<Vector3f> inRange = locationSet.stream()
                    .filter(loc -> loc.distanceSquared(actorPosition) <= maxDistanceSquared)
                    .sorted(Comparator.comparingDouble(v3f -> v3f.distanceSquared(actorPosition)))
                    .collect(Collectors.toList());
            if (inRange.isEmpty()) {
                findNearbyPlayersComponent.charactersWithinRange = Collections.emptyList();
                findNearbyPlayersComponent.closestCharacter = EntityRef.NULL;
                entity.saveComponent(findNearbyPlayersComponent);
                continue;
            }

            List<EntityRef> charactersWithinRange = inRange.stream().map(clientLocationMap::get).collect(Collectors.toList());

            //TODO: If the order of entities in the list has a meaning (e.g., closest to farthest) we should not check for set equality here.
            //      If the order is not important, we should just store the nearby entities as set in the component directly.
            if (!isEqual(charactersWithinRange, findNearbyPlayersComponent.charactersWithinRange)) {
                findNearbyPlayersComponent.charactersWithinRange = charactersWithinRange;
                findNearbyPlayersComponent.closestCharacter = charactersWithinRange.get(0);
                entity.saveComponent(findNearbyPlayersComponent);
            }
        }
    }

    /**
     * Compares two lists of entities with set-equality, i.e., whether they contain the same entities (in any order).
     */
    private boolean isEqual(List<EntityRef> one, List<EntityRef> two) {
        if ((one == null && two != null) || (one != null && two == null)) {
            return false;
        }
        if (one.size() != two.size()) {
            return false;
        }
        final Set<EntityRef> s1 = new HashSet<>(one);
        final Set<EntityRef> s2 = new HashSet<>(two);
        return s1.equals(s2);
    }
}
