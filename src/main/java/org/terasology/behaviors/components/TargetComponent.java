// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;

/**
 * A component used for when you want an entity to "target" another entity. The reason you may want to target an entity
 * can vary: a curious critter may turn to face a player who moves in close, and continue to track that player if they
 * stay close; a guard may turn towards and start to shoot a player who gets to close enough; a fixed place weapon may
 * also target a player in range.
 */
public class TargetComponent implements Component {
    public EntityRef target = EntityRef.NULL;
}
