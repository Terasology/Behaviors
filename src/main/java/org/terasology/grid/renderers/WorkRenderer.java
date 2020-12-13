// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.grid.renderers;

import org.joml.primitives.Rectanglei;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.grid.BlockRenderer;
import org.joml.Vector3i;
import org.terasology.minion.work.WorkTargetComponent;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.nui.Canvas;
import org.terasology.nui.Color;
import org.terasology.world.BlockEntityRegistry;

/**
 *
 */
@RegisterSystem
@Share(value = WorkRenderer.class)
public class WorkRenderer extends BaseComponentSystem implements BlockRenderer {
    @In
    private BlockEntityRegistry registry;

    @Override
    public void renderBlock(Canvas canvas, Vector3i blockPos, Rectanglei screenRegion) {
        EntityRef entity = registry.getExistingBlockEntityAt(blockPos);
        if (entity != null && entity.hasComponent(WorkTargetComponent.class)) {
            WorkTargetComponent workTargetComponent = entity.getComponent(WorkTargetComponent.class);

            canvas.drawLine(screenRegion.minX, screenRegion.minY, screenRegion.maxX, screenRegion.minY, Color.WHITE);
            canvas.drawLine(screenRegion.maxX, screenRegion.minY, screenRegion.maxX, screenRegion.maxY, Color.WHITE);
            canvas.drawLine(screenRegion.maxX, screenRegion.maxY, screenRegion.minX, screenRegion.maxY, Color.WHITE);
            canvas.drawLine(screenRegion.minX, screenRegion.maxY, screenRegion.minX, screenRegion.minY, Color.WHITE);

        }
    }
}
