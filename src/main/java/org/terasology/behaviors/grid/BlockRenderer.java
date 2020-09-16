// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.grid;

import org.joml.Rectanglei;
import org.joml.Vector3i;
import org.terasology.nui.Canvas;

/**
 *
 */
public interface BlockRenderer {
    void renderBlock(Canvas canvas, Vector3i blockPos, Rectanglei screenRegion);
}
