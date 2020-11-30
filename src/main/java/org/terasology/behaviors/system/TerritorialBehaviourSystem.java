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
package org.terasology.behaviors.system;

import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.behaviors.components.TerritoryDistance;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.location.LocationComponent;
import org.terasology.registry.In;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RegisterSystem(RegisterMode.AUTHORITY)
public class TerritorialBehaviourSystem extends BaseComponentSystem implements UpdateSubscriberSystem {
    @In
    public EntityManager entityManager;
    private List<Vector3f> territories = new ArrayList<Vector3f>();
    private Random random = new Random();
    private static Logger logger = LoggerFactory.getLogger(TerritorialBehaviourSystem.class);

    @Override
    public void initialise() {
        territories.clear();
    }

    /**
     * Update function for the Component System, which is called each
     * time the engine is updated.
     *
     * @param delta The time (in seconds) since the last engine update.
     */
    @Override
    public void update(float delta) {
        for (EntityRef entity : entityManager.getEntitiesWith(TerritoryDistance.class, LocationComponent.class)) {
            TerritoryDistance territoryDistance = entity.getComponent(TerritoryDistance.class);
            territoryDistance.distanceSquared = territoryDistance.location.distanceSquared(entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f()));
            entity.saveComponent(territoryDistance);
        }
    }

    @ReceiveEvent(components = {TerritoryDistance.class})
    public void onCreatureSpawned(OnActivatedComponent event, EntityRef creature) {
        TerritoryDistance territoryDistance = creature.getComponent(TerritoryDistance.class);
        territoryDistance.location = creature.getComponent(LocationComponent.class).getWorldPosition(new Vector3f());
        creature.saveComponent(territoryDistance);
    }
}
