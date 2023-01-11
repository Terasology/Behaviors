// Copyright 2023 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.module.behaviors.components;

import com.google.common.collect.Lists;
import org.terasology.engine.entitySystem.entity.EntityRef;

import java.util.List;

/**
 * If this components is attached to an NPC entity it will constantly look
 * around for nearby Nemesis that enter a given radius.
 */

public class FindNearbyNemesisComponent implements Component <FindNearbyNemesisComponent>{
    /* Search radius for finding nearby Nemesis */
    public float searchRadius = 10f;
    /* List of Nemesis entities nearby */
    public List<EntityRef> nemesisWithinRange;
    /* The Nemesis entity closest to the actor */
    public EntityRef closestNemesis;

    @Override
    public void copyFrom(FindNearbyNemesisComponent other) {
        this.searchRadius = other.searchRadius;
        this.nemesisWithinRange = Lists.newArrayList(other.nemesisWithinRange);
        this.closestNemesis = other.closestNemesis;
    }
}
