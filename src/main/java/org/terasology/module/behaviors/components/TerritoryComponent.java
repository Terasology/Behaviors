// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.components;

import org.joml.Vector3f;
import org.terasology.engine.registry.In;
import org.terasology.gestalt.entitysystem.component.Component;

public class TerritoryComponent implements Component<TerritoryComponent> {
    public float maxDistance;       //Distance for Chasing
    public float distanceSquared;   //Current Distance from Territory Mid Point
    public float radius;            //Radius of the Territory
    public Vector3f location;       //MidPoint of the Territory

    @Override
    public void copyFrom(TerritoryComponent other) {
        this.distanceSquared = other.distanceSquared;
        this.location = new Vector3f(other.location);
        // Should this be done in a logic or can this be done in initialisation?
        this.maxDistance = other.maxDistance * other.maxDistance;
        this.radius = other.radius * other.radius;
    }
}
