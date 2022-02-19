// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.plugin;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.characters.CharacterMoveInputEvent;
import org.terasology.engine.logic.characters.CharacterMovementComponent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.world.WorldProvider;
import org.terasology.flexiblepathfinding.plugins.JPSPlugin;
import org.terasology.flexiblepathfinding.plugins.basic.LeapingPlugin;

public class LeapingMovementPlugin extends MovementPlugin {
    private static final Logger logger = LoggerFactory.getLogger(LeapingMovementPlugin.class);

    public LeapingMovementPlugin(WorldProvider world, Time time) {
        super(world, time);
    }

    public LeapingMovementPlugin() {
        super();
    }

    @Override
    public JPSPlugin getJpsPlugin(EntityRef entity) {
        CharacterMovementComponent component = entity.getComponent(CharacterMovementComponent.class);
        return new LeapingPlugin(getWorld(), component.radius * 2.0f, component.height);
    }

    @Override
    public CharacterMoveInputEvent move(EntityRef entity, Vector3fc dest, int sequence) {
        if (logger.isDebugEnabled()) {
            logger.debug("--> [{}] {} move: {} - current: {} - destination: {}",
                    entity.getId(),
                    String.format("%03d", sequence),
                    "leaping",
                    entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f()),
                    dest);
        }
        Vector3f delta = getDelta(entity, dest);
        float yaw = getYaw(delta);
        long dt = getTime().getGameDeltaInMs();

        CharacterMovementComponent movement = entity.getComponent(CharacterMovementComponent.class);
        return new CharacterMoveInputEvent(sequence, 0, yaw, delta, false, false, true, dt);
    }
}
