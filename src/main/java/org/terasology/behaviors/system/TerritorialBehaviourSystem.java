// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.system;

import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.behaviors.components.TerritoryDistance;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.In;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RegisterSystem(RegisterMode.AUTHORITY)
public class TerritorialBehaviourSystem extends BaseComponentSystem implements UpdateSubscriberSystem {
    
    private static Logger logger = LoggerFactory.getLogger(TerritorialBehaviourSystem.class);

    @In
    public EntityManager entityManager;
    private List<Vector3f> territories = new ArrayList<Vector3f>();
    private Random random = new Random();
    

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
            territoryDistance.distanceSquared = territoryDistance.location.distanceSquared(
                entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f()));
            entity.saveComponent(territoryDistance);
        }
    }

    @ReceiveEvent(components = TerritoryDistance.class)
    public void onCreatureSpawned(OnActivatedComponent event, EntityRef creature) {
        TerritoryDistance territoryDistance = creature.getComponent(TerritoryDistance.class);
        territoryDistance.location = creature.getComponent(LocationComponent.class).getWorldPosition(new Vector3f());
        creature.saveComponent(territoryDistance);
    }
}
