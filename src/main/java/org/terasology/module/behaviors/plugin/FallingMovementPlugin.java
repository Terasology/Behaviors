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
import org.terasology.flexiblepathfinding.plugins.basic.FallingPlugin;

public class FallingMovementPlugin extends MovementPlugin {

    public FallingMovementPlugin(WorldProvider world, Time time) {
        super(world, time);
    }

    public FallingMovementPlugin() {
        super();
    }

    @Override
    public JPSPlugin getJpsPlugin(EntityRef entity) {
        CharacterMovementComponent component = entity.getComponent(CharacterMovementComponent.class);
        return new FallingPlugin(getWorld(), component.radius * 2.0f, component.height);
    }

    @Override
    public CharacterMoveInputEvent move(EntityRef entity, Vector3fc dest, int sequence) {
        CharacterMovementComponent movement = entity.getComponent(CharacterMovementComponent.class);
        // The other basic plugins assume that the entity is not affected by gravity. However, in this specific case of falling,
        // we actually want gravity on the NPC. Therefore, we need to make sure that we're in a movement mode that has non-zero
        // gravity factor, e.g., WALKING.
        if (movement.mode != MovementMode.WALKING) {
            entity.send(new SetMovementModeEvent(MovementMode.WALKING));
        }
        //TODO: ensure that 'dest' is below the entity's location? What should we do in case we missed the 'dest'?
        long dt = getTime().getGameDeltaInMs();

        return new CharacterMoveInputEvent(sequence, 0, 0, new Vector3f(), false, false, false, dt);
    }
}
