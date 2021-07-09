// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion;

import org.terasology.gestalt.entitysystem.component.Component;

/**
 *
 */
public class BuildWallComponent implements Component<BuildWallComponent> {
    public String blockType;

    @Override
    public void copy(BuildWallComponent other) {
        this.blockType = other.blockType;
    }
}
