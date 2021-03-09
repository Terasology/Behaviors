// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;

/**
 * Entities with this components want to follow another entity. Used to make gooey follow the player around if
 * the player wishes so.
 */
public class FollowComponent implements Component {
    public EntityRef entityToFollow = EntityRef.NULL;
}
