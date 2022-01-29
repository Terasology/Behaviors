// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.plugin;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.characters.CharacterMoveInputEvent;
import org.terasology.engine.logic.characters.CharacterMovementComponent;
import org.terasology.engine.logic.characters.MovementMode;
import org.terasology.engine.logic.characters.events.SetMovementModeEvent;
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
        CharacterMovementComponent movement = entity.getComponent(CharacterMovementComponent.class);
        // The underlying WalkingPlugin assumes that entities are not affected by gravity.
        // To simulate this, we'll use the FLYING movement mode for all entities when moving them with this plugin.
        if (movement.mode != MovementMode.FLYING) {
            entity.send(new SetMovementModeEvent(MovementMode.FLYING));
        }

        Vector3f delta = getDelta(entity, dest);
        float yaw = getYaw(delta);
        long dt = getTime().getGameDeltaInMs();

        return new CharacterMoveInputEvent(sequence, 0, yaw, delta, false, false, false, dt);
    }
}
