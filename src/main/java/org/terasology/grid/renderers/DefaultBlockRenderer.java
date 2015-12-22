/*
 * Copyright 2015 MovingBlocks
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

import org.terasology.asset.Assets;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.grid.BlockRenderer;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector3i;
import org.terasology.math.geom.Vector2f;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.rendering.assets.texture.Texture;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.Color;
import org.terasology.rendering.nui.ScaleMode;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockPart;
import org.terasology.world.block.loader.WorldAtlas;

/**
 * Created by synopia on 12.02.14.
 */
@RegisterSystem
@Share(value = DefaultBlockRenderer.class)
public class DefaultBlockRenderer extends BaseComponentSystem implements BlockRenderer {
    @In
    private WorldProvider worldProvider;
    @In
    private WorldAtlas worldAtlas;

    private Texture terrainTex;
    private float relativeTileSize;

    @Override
    public void initialise() {
        terrainTex = Assets.getTexture("engine:terrain");
        relativeTileSize = 0.0625f;
    }

    @Override
    public void renderBlock(Canvas canvas, Vector3i blockPos, Rect2i screenRegion) {
        Color color = new Color(1f, 1f, 1f, 1f);
        int depth = 0;
        blockPos.y++;
        float max = 10;
        while (blockPos.y >= 0) {
            Block block = worldProvider.getBlock(blockPos);

            if (!block.isTranslucent()) {
                Vector2f textureAtlasPos = block.getPrimaryAppearance().getTextureAtlasPos(BlockPart.TOP);
                canvas.drawTextureRaw(terrainTex, screenRegion, color, ScaleMode.SCALE_FILL,
                        textureAtlasPos.x, textureAtlasPos.y, relativeTileSize, relativeTileSize);
                break;
            } else {
                // process todo alpha blocks
            }
            blockPos.y--;
            depth++;
            if (depth >= max) {
                break;
            }
            color = new Color(1 - depth / max, 1 - depth / max, 1 - depth / max, 1);
        }

    }
}
