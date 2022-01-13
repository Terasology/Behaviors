// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.components;

import org.terasology.gestalt.entitysystem.component.Component;

public class BuildWallComponent implements Component<BuildWallComponent> {
    public String blockType;

    @Override
    public void copyFrom(BuildWallComponent other) {
        this.blockType = other.blockType;
    }
}
