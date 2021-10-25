// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.grid;

import org.joml.Vector3i;
import org.terasology.joml.geom.Rectanglei;
import org.terasology.nui.Canvas;

public interface BlockRenderer {
    void renderBlock(Canvas canvas, Vector3i blockPos, Rectanglei screenRegion);
}
