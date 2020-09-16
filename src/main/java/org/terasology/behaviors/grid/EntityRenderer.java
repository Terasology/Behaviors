// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.grid;

import org.joml.Rectanglei;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.nui.Canvas;

/**
 *
 */
public interface EntityRenderer {
    void renderBlock(Canvas canvas, EntityRef entity, Rectanglei screenRegion);
}
