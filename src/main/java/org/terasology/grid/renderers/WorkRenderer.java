// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.grid.renderers;

import org.joml.Vector3i;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.engine.registry.Share;
import org.terasology.engine.world.BlockEntityRegistry;
import org.terasology.grid.BlockRenderer;
import org.terasology.joml.geom.Rectanglei;
import org.terasology.minion.work.WorkTargetComponent;
import org.terasology.nui.Canvas;
import org.terasology.nui.Color;

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
