// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.math.geom.Vector3f;

public class TerritoryDistance implements Component {
    public float distanceSquared;
    public Vector3f location;
}
