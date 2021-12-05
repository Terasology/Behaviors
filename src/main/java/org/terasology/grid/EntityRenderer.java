// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.grid;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.joml.geom.Rectanglei;
import org.terasology.nui.Canvas;

public interface EntityRenderer {
    void renderBlock(Canvas canvas, EntityRef entity, Rectanglei screenRegion);
}
