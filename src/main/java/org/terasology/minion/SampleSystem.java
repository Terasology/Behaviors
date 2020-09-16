// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.selection.ApplyBlockSelectionEvent;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.WorldProvider;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.math.geom.Vector3i;

/**
 *
 */
@RegisterSystem
public class SampleSystem extends BaseComponentSystem {
    @In
    private WorldProvider worldProvider;
    @In
    private BlockManager blockManager;

    @ReceiveEvent
    public void onSelection(ApplyBlockSelectionEvent event, EntityRef entity) {
        EntityRef itemEntity = event.getSelectedItemEntity();
        BuildWallComponent buildWallComponent = itemEntity.getComponent(BuildWallComponent.class);
        if (buildWallComponent == null) {
            return;
        }
        Block solid = blockManager.getBlock(buildWallComponent.blockType);
        Vector3i size = event.getSelection().size();
        Vector3i pos = event.getSelection().min();
        for (int z = 0; z < size.z; z++) {
            for (int y = 0; y < size.y; y++) {
                for (int x = 0; x < size.x; x++) {
                    worldProvider.setBlock(new Vector3i(pos.x + x, pos.y + y, pos.z + z), solid);
                }
            }
        }
    }

    @Override
    public void initialise() {
    }

    @Override
    public void shutdown() {
    }
}
