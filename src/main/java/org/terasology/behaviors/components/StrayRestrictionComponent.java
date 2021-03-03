// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.components;

import org.terasology.entitySystem.Component;
import org.terasology.world.block.BlockArea;
import org.terasology.world.block.BlockAreac;

/**
 * Attached to characters that use the {@link org.terasology.behaviors.actions.NearbyBlockRestricted} behavior, which
 * defines the world region that the character is allowed to stray in.
 */
public class StrayRestrictionComponent implements Component {

    /**
     * The region that this character is allowed to stray in. Defines an x&z area in world space.
     */
    public BlockArea allowedRegion = new BlockArea(BlockArea.INVALID);

    public StrayRestrictionComponent(BlockAreac allowedRegion) {
        this.allowedRegion.set(allowedRegion);
    }

    public StrayRestrictionComponent() { }

}
