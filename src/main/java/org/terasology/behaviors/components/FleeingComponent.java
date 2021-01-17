// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.components;

import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;

/**
 * This components is used by the FleeOnHit and FleeInProximity module to allow an
 * NPC to exhibit the Flee behavior
 */
public class FleeingComponent implements Component {
    /* Minimum distance from instigator after which the NPC will stop 'flee'ing */
    public float minDistance = 10f;
    /* Entity to run away from */
    public EntityRef instigator;
}
