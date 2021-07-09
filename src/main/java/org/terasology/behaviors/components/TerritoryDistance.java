// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.components;

import org.joml.Vector3f;
import org.terasology.gestalt.entitysystem.component.Component;

public class TerritoryDistance implements Component<TerritoryDistance> {
    public float distanceSquared;
    public Vector3f location;

    @Override
    public void copy(TerritoryDistance other) {
        this.distanceSquared = other.distanceSquared;
        this.location = new Vector3f(other.location);
    }
}
