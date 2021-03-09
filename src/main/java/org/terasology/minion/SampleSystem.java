// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion;

import org.joml.Vector3ic;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.inventory.InventoryComponent;
import org.terasology.engine.logic.players.event.OnPlayerSpawnedEvent;
import org.terasology.engine.logic.selection.ApplyBlockSelectionEvent;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.WorldProvider;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockManager;

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
        for (Vector3ic pos : event.getSelection()) {
            worldProvider.setBlock(pos, solid);
        }
    }

    @Override
    public void initialise() {
    }

    @Override
    public void shutdown() {
    }
}
