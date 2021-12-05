// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion.work;

import org.joml.Vector3ic;
import org.terasology.engine.core.SimpleUri;
import org.terasology.engine.entitySystem.entity.EntityRef;

import java.util.List;

/**
 * Defines a work on a block.
 */
public interface Work {
    /**
     * Returns list of positions that are valid to work on this work.
     */
    List<Vector3ic> getTargetPositions(EntityRef block);

    boolean canMinionWork(EntityRef block, EntityRef minion);

    boolean isAssignable(EntityRef block);

    void letMinionWork(EntityRef block, EntityRef minion);

    boolean isRequestable(EntityRef block);

    SimpleUri getUri();

    float cooldownTime();
}
