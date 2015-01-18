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

import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.grid.BlockRenderer;
import org.terasology.math.Rect2i;
import org.terasology.math.Vector3i;
import org.terasology.navgraph.NavGraphSystem;
import org.terasology.navgraph.WalkableBlock;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.Color;

/**
 * Created by synopia on 12.02.14.
 */
@RegisterSystem
@Share(value = WalkableBlockRenderer.class)
public class WalkableBlockRenderer extends BaseComponentSystem implements BlockRenderer {
    @In
    private NavGraphSystem navGraphSystem;

    @Override
    public void renderBlock(Canvas canvas, Vector3i blockPos, Rect2i screenRegion) {
        WalkableBlock block = navGraphSystem.getBlock(blockPos);
        if (block != null) {
            canvas.drawLine(screenRegion.minX(), screenRegion.minY(), screenRegion.minX() + 1, screenRegion.minY(), Color.GREEN);
        }
    }
}
