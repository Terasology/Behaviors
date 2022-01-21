// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.systems;

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
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.module.behaviors.components.MinionMoveComponent;

import java.util.Map;


@Share(MinionMoveSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class MinionMoveSystem extends BaseComponentSystem implements UpdateSubscriberSystem {
    private static final Logger logger = LoggerFactory.getLogger(MinionMoveSystem.class);

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
                                        MinionMoveComponent minionMoveComponent) {
        if (minionMoveComponent == null) {
            return;
        }

        minionMoveComponent.collidedHorizontally = true;
    }

    public void enqueue(EntityRef entity, CharacterMoveInputEvent event) {
        eventQueue.put(entity, event);
    }
}
