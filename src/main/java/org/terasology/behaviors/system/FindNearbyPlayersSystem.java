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
package org.terasology.behaviors.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.characters.AliveCharacterComponent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.network.ClientComponent;
import org.terasology.registry.In;
import org.terasology.behaviors.components.FindNearbyPlayersComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RegisterSystem(RegisterMode.AUTHORITY)
public class FindNearbyPlayersSystem extends BaseComponentSystem implements UpdateSubscriberSystem {


    private static final Logger logger = LoggerFactory.getLogger(FindNearbyPlayersSystem.class);

    @In
    private EntityManager entityManager;

    @Override
    public void update(float delta) {
        Iterable<EntityRef> clients = entityManager.getEntitiesWith(ClientComponent.class);
        Map<Vector3f, EntityRef> clientLocationMap = new HashMap<>();

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
            clientLocationMap.put(locationComponent.getWorldPosition(), character);
        }
        Set<Vector3f> locationSet = clientLocationMap.keySet();

        for (EntityRef entity : entityManager.getEntitiesWith(FindNearbyPlayersComponent.class)) {
            Vector3f actorPosition = entity.getComponent(LocationComponent.class).getWorldPosition();
            FindNearbyPlayersComponent findNearbyPlayersComponent = entity.getComponent(FindNearbyPlayersComponent.class);
            float maxDistance = findNearbyPlayersComponent.searchRadius;
            float maxDistanceSquared = maxDistance * maxDistance;

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

            List<EntityRef> charactersWithinRange = 
            	inRange.stream().map(clientLocationMap::get).collect(Collectors.toList());;

            if (!isEqual(charactersWithinRange, findNearbyPlayersComponent.charactersWithinRange)) {
                findNearbyPlayersComponent.charactersWithinRange = charactersWithinRange;
                findNearbyPlayersComponent.closestCharacter = charactersWithinRange.get(0);
                entity.saveComponent(findNearbyPlayersComponent);
            }
        }
    }

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
