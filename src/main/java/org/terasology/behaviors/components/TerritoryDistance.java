// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.components;

import org.joml.Vector3f;
import org.terasology.entitySystem.Component;

public class TerritoryDistance implements Component {
    public float distanceSquared;
    public Vector3f location;
}
