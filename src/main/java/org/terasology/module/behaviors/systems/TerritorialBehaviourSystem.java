// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.systems;

import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.core.Time;
import org.terasology.module.behaviors.components.TerritoryComponent;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.In;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

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

    // Unnecessary if delta is enough for the intervall.
    private static float CHECK_INTERVALL = 200;
    private float lastCheckTime = 0;

    @In
    private Time timer;
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

        long gameTimeInMS = timer.getGameTimeInMs();

        if(lastCheckTime + CHECK_INTERVALL < gameTimeInMS) {

            for (EntityRef entity : entityManager.getEntitiesWith(TerritoryComponent.class, LocationComponent.class)) {

                    TerritoryComponent territoryComponent = entity.getComponent(TerritoryComponent.class);
                    // This is basically distance squared? And continuously updated
                    territoryComponent.distanceSquared = territoryComponent.location.distanceSquared(
                            entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f()));

                    lastCheckTime = gameTimeInMS;
                    entity.saveComponent(territoryComponent);
                }
            }
        }

    @ReceiveEvent(components = TerritoryComponent.class)
    public void onCreatureSpawned(OnActivatedComponent event, EntityRef creature) {

        TerritoryComponent territoryComponent = creature.getComponent(TerritoryComponent.class);
        territoryComponent.location = creature.getComponent(LocationComponent.class).getWorldPosition(new Vector3f());
        creature.saveComponent(territoryComponent);
    }
}
