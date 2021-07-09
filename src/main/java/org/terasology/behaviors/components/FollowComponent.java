// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.components;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Entities with this components want to follow another entity. Used to make gooey follow the player around if
 * the player wishes so.
 */
public class FollowComponent implements Component<FollowComponent> {
    public EntityRef entityToFollow = EntityRef.NULL;

    @Override
    public void copy(FollowComponent other) {
        this.entityToFollow = other.entityToFollow;
    }
}
