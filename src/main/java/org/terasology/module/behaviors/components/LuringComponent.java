// Copyright 2023 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.module.behaviors.components;

import com.google.common.collect.Lists;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.List;

/**
 * This components shall be used by the combinedCritter module to allow an NPC to exhibit the lure behavior
 */

public class LuringComponent implements Component<LuringComponent> {

    //List of Items for a Luring Stat
    // Configurable for e.g.
    //"CoreAssets:TallGrass1"
    public List<String> luringItems = Lists.newArrayList();

    @Override
    public void copyFrom(LuringComponent other) {
        this.luringItems = Lists.newArrayList(other.luringItems);
    }
}
