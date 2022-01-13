// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblemovement.system;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.engine.logic.characters.CharacterMoveInputEvent;
import org.terasology.engine.logic.characters.events.HorizontalCollisionEvent;
import org.terasology.engine.registry.Share;
import org.terasology.flexiblemovement.FlexibleMovementComponent;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

import java.util.Map;


@Share(FlexibleMovementSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class FlexibleMovementSystem extends BaseComponentSystem implements UpdateSubscriberSystem {
    private static final Logger logger = LoggerFactory.getLogger(FlexibleMovementSystem.class);

    private final Map<EntityRef, CharacterMoveInputEvent> eventQueue = Maps.newHashMap();

    @Override
    public void update(float delta) {
        for (Map.Entry<EntityRef, CharacterMoveInputEvent> entry : eventQueue.entrySet()) {
            if (entry.getKey() != null && entry.getKey().exists()) {
                entry.getKey().send(entry.getValue());
            }
        }
        eventQueue.clear();
    }

    @ReceiveEvent
    public void markHorizontalCollision(HorizontalCollisionEvent event, EntityRef entity,
                                        FlexibleMovementComponent flexibleMovementComponent) {
        if (flexibleMovementComponent == null) {
            return;
        }

        flexibleMovementComponent.collidedHorizontally = true;
    }

    public void enqueue(EntityRef entity, CharacterMoveInputEvent event) {
        eventQueue.put(entity, event);
    }
}
