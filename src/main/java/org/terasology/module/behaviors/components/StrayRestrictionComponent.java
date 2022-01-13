// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.components;

import org.terasology.engine.world.block.BlockArea;
import org.terasology.engine.world.block.BlockAreac;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.module.behaviors.actions.NearbyBlockRestricted;

/**
 * Attached to characters that use the {@link NearbyBlockRestricted} behavior, which
 * defines the world region that the character is allowed to stray in.
 */
public class StrayRestrictionComponent implements Component<StrayRestrictionComponent> {

    /**
     * The region that this character is allowed to stray in. Defines an x&z area in world space.
     */
    public BlockArea allowedRegion = new BlockArea(BlockArea.INVALID);

    public StrayRestrictionComponent(BlockAreac allowedRegion) {
        this.allowedRegion.set(allowedRegion);
    }

    public StrayRestrictionComponent() { }

    @Override
    public void copyFrom(StrayRestrictionComponent other) {
        this.allowedRegion.set(other.allowedRegion);

    }
}
