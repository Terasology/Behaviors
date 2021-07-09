// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.components;

import org.terasology.gestalt.entitysystem.component.Component;

/**
 * If this components is attached to an NPC entity it will exhibit the flee-on-hit behavior
 * When hit, the NPC will run with a speed of `speedMultiplier`*normalSpeed
 * till it is at a safe `minDistance` from the damage inflicter- `instigator`.
 * When it reaches a safe distance the instigator is set to null.
 */
public class FleeOnHitComponent implements Component<FleeOnHitComponent> {
    /* Minimum distance from instigator after which the NPC will stop 'flee'ing */
    public float minDistance = 10f;
    /* Speed factor by which flee speed increases */
    public float speedMultiplier = 1.2f;

    @Override
    public void copy(FleeOnHitComponent other) {
        this.minDistance = other.minDistance;
        this.speedMultiplier = other.speedMultiplier;
    }
}
