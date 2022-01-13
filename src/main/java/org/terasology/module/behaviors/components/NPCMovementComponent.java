// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.components;

import org.joml.Vector3f;
import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Non player characters should have this components even if they don't move, so that movement input gets simulated fo
 * them. Otherwise the game will try to predict the character movmeent based on the last movement which leads to strange
 * behavior.
 */
public class NPCMovementComponent implements Component<NPCMovementComponent> {
    /**
     * Angle in degree in which the character will look at. Used to simulate constant yaw input. Calculating it
     * freshly each time would lead to rounding mistakes that commulate.
     */
    public float yaw;

    /**
     * Position to which the charcter will move to in a straight line. For advanced pathing, a behavior tree should
     * be used that sets this field to the next waypoint of the calculated path to the target.
     */
    public transient Vector3f targetPosition;

    @Override
    public void copyFrom(NPCMovementComponent other) {
        this.yaw = other.yaw;
        this.targetPosition = new Vector3f(other.targetPosition);
    }
}
