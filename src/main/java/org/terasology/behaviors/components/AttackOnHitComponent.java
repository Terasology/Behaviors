// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;

public class AttackOnHitComponent implements Component {
    // Maximum distance from instigator after which the animal will stop chasing to attack
    public float maxDistance = 10f;
    // Speed factor by which attack speed increases
    public float speedMultiplier = 1.2f;
    public EntityRef instigator;
    public long timeWhenHit;
}