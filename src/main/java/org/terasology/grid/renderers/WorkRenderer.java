/*
 * Copyright 2014 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.grid.renderers;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.grid.BlockRenderer;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector3i;
import org.terasology.minion.work.WorkTargetComponent;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.Color;
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
    public void renderBlock(Canvas canvas, Vector3i blockPos, Rect2i screenRegion) {
        EntityRef entity = registry.getExistingBlockEntityAt(blockPos);
        if (entity != null && entity.hasComponent(WorkTargetComponent.class)) {
            WorkTargetComponent workTargetComponent = entity.getComponent(WorkTargetComponent.class);

            canvas.drawLine(screenRegion.minX(), screenRegion.minY(), screenRegion.maxX(), screenRegion.minY(), Color.WHITE);
            canvas.drawLine(screenRegion.maxX(), screenRegion.minY(), screenRegion.maxX(), screenRegion.maxY(), Color.WHITE);
            canvas.drawLine(screenRegion.maxX(), screenRegion.maxY(), screenRegion.minX(), screenRegion.maxY(), Color.WHITE);
            canvas.drawLine(screenRegion.minX(), screenRegion.maxY(), screenRegion.minX(), screenRegion.minY(), Color.WHITE);

        }
    }
}
