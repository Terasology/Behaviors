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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.grid.EntityRenderer;
import org.terasology.logic.characters.CharacterComponent;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.TeraMath;
import org.terasology.minion.move.MinionMoveComponent;
import org.terasology.registry.Share;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.Color;

/**
 * Created by synopia on 12.02.14.
 */
@RegisterSystem
@Share(value = EntityRenderer.class)
public class DefaultEntityRenderer extends BaseComponentSystem implements EntityRenderer {
    private static final Logger logger = LoggerFactory.getLogger(DefaultEntityRenderer.class);

    @Override
    public void renderBlock(Canvas canvas, EntityRef entity, Rect2i screenRegion) {
        CharacterComponent characterComponent = entity.getComponent(CharacterComponent.class);

        // float yaw = TeraMath.PI / 2 - characterComponent.yaw / 180.f * TeraMath.PI;

        float yaw = 0.5f; // I'm going to hell, TODO resolve yaw
        int w = screenRegion.maxX() - screenRegion.minX();
        int h = screenRegion.maxY() - screenRegion.minY();
        int x0 = screenRegion.minX() + w / 2 + (int) (Math.cos(yaw) * w);
        int y0 = screenRegion.minY() + h / 2 + (int) (Math.sin(yaw) * h);
        int x1 = screenRegion.minX() + w / 2 + (int) (Math.cos(yaw + Math.PI / 2) * w);
        int y1 = screenRegion.minY() + h / 2 + (int) (Math.sin(yaw + Math.PI / 2) * h);
        int x2 = screenRegion.minX() + w / 2 + (int) (Math.cos(yaw - Math.PI / 2) * w);
        int y2 = screenRegion.minY() + h / 2 + (int) (Math.sin(yaw - Math.PI / 2) * h);

        Color color = Color.YELLOW;
        if (entity.hasComponent(MinionMoveComponent.class)) {
            color = Color.BLUE;
        }
        canvas.drawLine(x0, y0, x1, y1, color);
        canvas.drawLine(x0, y0, x2, y2, color);
        canvas.drawLine(x1, y1, x2, y2, color);
    }
}
