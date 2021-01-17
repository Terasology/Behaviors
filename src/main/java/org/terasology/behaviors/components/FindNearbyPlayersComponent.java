// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.components;

import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;

import java.util.List;

/**
 * If this components is attached to an NPC entity it will constantly look
 * around for nearby players that enter a given radius.
 */
public class FindNearbyPlayersComponent implements Component {
    /* Search radius for finding nearby players */
    public float searchRadius = 10f;
    /* List of player entities nearby */
    public List<EntityRef> charactersWithinRange;
    /* The player entity closest to the actor */
    public EntityRef closestCharacter;
}
