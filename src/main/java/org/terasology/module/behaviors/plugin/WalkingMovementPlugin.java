// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.plugin;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.characters.CharacterMoveInputEvent;
import org.terasology.engine.logic.characters.CharacterMovementComponent;
import org.terasology.engine.world.WorldProvider;
import org.terasology.flexiblepathfinding.plugins.JPSPlugin;
import org.terasology.flexiblepathfinding.plugins.basic.WalkingPlugin;

public class WalkingMovementPlugin extends MovementPlugin {
    public WalkingMovementPlugin(WorldProvider world, Time time) {
        super(world, time);
    }

    public WalkingMovementPlugin() {
        super();
    }

    @Override
    public JPSPlugin getJpsPlugin(EntityRef entity) {
        CharacterMovementComponent component = entity.getComponent(CharacterMovementComponent.class);
        return new WalkingPlugin(getWorld(), component.radius * 2.0f, component.height);
    }

    @Override
    public CharacterMoveInputEvent move(EntityRef entity, Vector3fc dest, int sequence) {
        Vector3f delta = getDelta(entity, dest);
        float yaw = getYaw(delta);
        long dt = getTime().getGameDeltaInMs();

        CharacterMovementComponent movement = entity.getComponent(CharacterMovementComponent.class);
        return new CharacterMoveInputEvent(sequence, 0, yaw, delta, false, false, false, dt);
    }
}
