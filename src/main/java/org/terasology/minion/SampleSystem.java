// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.logic.players.event.OnPlayerSpawnedEvent;
import org.terasology.logic.selection.ApplyBlockSelectionEvent;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;

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
    public void onPlayerSpawn(OnPlayerSpawnedEvent event, EntityRef player, InventoryComponent inventory) {
    }

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
