// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.components;

import com.google.common.collect.Lists;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.List;

/**
 * If this components is attached to an NPC entity it will constantly look
 * around for nearby players that enter a given radius.
 */
public class FindNearbyPlayersComponent implements Component<FindNearbyPlayersComponent> {
    /* Search radius for finding nearby players */
    public float searchRadius = 10f;
    /* List of player entities nearby */
    public List<EntityRef> charactersWithinRange;
    /* The player entity closest to the actor */
    public EntityRef closestCharacter;

    @Override
    public void copy(FindNearbyPlayersComponent other) {
        this.searchRadius = other.searchRadius;
        this.charactersWithinRange = Lists.newArrayList(other.charactersWithinRange);
        this.closestCharacter = other.closestCharacter;
    }
}
