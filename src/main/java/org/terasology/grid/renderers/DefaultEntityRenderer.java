// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.grid.renderers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.characters.CharacterComponent;
import org.terasology.engine.registry.Share;
import org.terasology.grid.EntityRenderer;
import org.terasology.joml.geom.Rectanglei;
import org.terasology.minion.move.MinionMoveComponent;
import org.terasology.nui.Canvas;
import org.terasology.nui.Color;

@RegisterSystem
@Share(value = EntityRenderer.class)
public class DefaultEntityRenderer extends BaseComponentSystem implements EntityRenderer {
    private static final Logger logger = LoggerFactory.getLogger(DefaultEntityRenderer.class);

    @Override
    public void renderBlock(Canvas canvas, EntityRef entity, Rectanglei screenRegion) {
        CharacterComponent characterComponent = entity.getComponent(CharacterComponent.class);

        // float yaw = TeraMath.PI / 2 - characterComponent.yaw / 180.f * TeraMath.PI;

        float yaw = 0.5f; // I'm going to hell, TODO resolve yaw
        int w = screenRegion.maxX - screenRegion.minX;
        int h = screenRegion.maxY - screenRegion.minY;
        int x0 = screenRegion.minX + w / 2 + (int) (Math.cos(yaw) * w);
        int y0 = screenRegion.minY + h / 2 + (int) (Math.sin(yaw) * h);
        int x1 = screenRegion.minX + w / 2 + (int) (Math.cos(yaw + Math.PI / 2) * w);
        int y1 = screenRegion.minY + h / 2 + (int) (Math.sin(yaw + Math.PI / 2) * h);
        int x2 = screenRegion.minX + w / 2 + (int) (Math.cos(yaw - Math.PI / 2) * w);
        int y2 = screenRegion.minY + h / 2 + (int) (Math.sin(yaw - Math.PI / 2) * h);

        Color color = Color.YELLOW;
        if (entity.hasComponent(MinionMoveComponent.class)) {
            color = Color.BLUE;
        }
        canvas.drawLine(x0, y0, x1, y1, color);
        canvas.drawLine(x0, y0, x2, y2, color);
        canvas.drawLine(x1, y1, x2, y2, color);
    }
}
