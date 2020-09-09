// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.math.geom.Rect2i;

/**
 * Attached to characters that use the {@link org.terasology.behaviors.actions.NearbyBlockRestricted} behavior, which
 * defines the world region that the character is allowed to stray in.
 */
public class StrayRestrictionComponent implements Component {

    /**
     * The region that this character is allowed to stray in. Defines an x&z area in world space.
     */
    public Rect2i allowedRegion;

    public StrayRestrictionComponent(Rect2i allowedRegion) {
        this.allowedRegion = allowedRegion;
    }

    public StrayRestrictionComponent() {
    }

}
