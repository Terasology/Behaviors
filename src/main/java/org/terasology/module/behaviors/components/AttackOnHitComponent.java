// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.components;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.component.Component;

public class AttackOnHitComponent implements Component<AttackOnHitComponent> {
    // Maximum distance from instigator after which the animal will stop chasing to attack
    public float maxDistance = 10f;
    // Speed factor by which attack speed increases
    public float speedMultiplier = 1.2f;
    public EntityRef instigator;
    public long timeWhenHit;

    @Override
    public void copyFrom(AttackOnHitComponent other) {
        this.maxDistance = other.maxDistance;
        this.speedMultiplier = other.speedMultiplier;
        this.instigator = other.instigator;
        this.timeWhenHit = other.timeWhenHit;
    }
}
