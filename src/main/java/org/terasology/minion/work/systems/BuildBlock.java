// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion.work.systems;

import com.google.common.collect.Lists;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.engine.core.SimpleUri;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.ComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.WorldProvider;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockComponent;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.minion.work.Work;
import org.terasology.minion.work.WorkFactory;
import org.terasology.minion.work.WorkTargetComponent;
import org.terasology.navgraph.WalkableBlock;
import org.terasology.pathfinding.componentSystem.PathfinderSystem;

import java.util.List;

@RegisterSystem
public class BuildBlock extends BaseComponentSystem implements Work, ComponentSystem {
    private static final int[][] DIRECT_NEIGHBORS = new int[][]{
        {-1, 0, 0}, {1, 0, 0}, {0, -1, 0}, {0, 1, 0}, {0, 0, -1}, {0, 0, 1}
    };
    private final SimpleUri uri;
    @In
    private PathfinderSystem pathfinderSystem;
    @In
    private WorkFactory workFactory;
    @In
    private WorldProvider worldProvider;
    @In
    private BlockManager blockManager;
    private Block blockType;

    public BuildBlock() {
        uri = new SimpleUri("Behaviors:buildBlock");
    }

    @Override
    public void initialise() {
        workFactory.register(this);
        //TODO: set this based on configuration settings instead
        setBlock("CoreAssets:Dirt");
    }

    @Override
    public void shutdown() {

    }

    @Override
    public SimpleUri getUri() {
        return uri;
    }

    public List<Vector3ic> getTargetPositions(EntityRef block) {
        List<Vector3ic> targetPositions = Lists.newArrayList();
        if (block == null || !block.hasComponent(BlockComponent.class) || blockType == null) {
            return targetPositions;
        }
        Vector3i position = block.getComponent(BlockComponent.class).getPosition(new Vector3i());
        position.y--;
        WalkableBlock walkableBlock = pathfinderSystem.getBlock(position);
        if (walkableBlock != null) {
            targetPositions.add(walkableBlock.getBlockPosition());
        }

        return targetPositions;
    }

    @Override
    public boolean canMinionWork(EntityRef block, EntityRef minion) {
        WalkableBlock actualBlock = pathfinderSystem.getBlock(minion);
        Vector3i position = block.getComponent(BlockComponent.class).getPosition(new Vector3i());
        position.y--;
        WalkableBlock expectedBlock = pathfinderSystem.getBlock(position);
        return actualBlock == expectedBlock && blockType != null;
    }

    @Override
    public boolean isAssignable(EntityRef block) {
        Vector3i position = block.getComponent(BlockComponent.class).getPosition(new Vector3i());
        Block type = worldProvider.getBlock(position);
        return type.isPenetrable() && blockType != null;
    }

    @Override
    public void letMinionWork(EntityRef block, EntityRef minion) {
        block.removeComponent(WorkTargetComponent.class);
        Vector3ic pos = block.getComponent(BlockComponent.class).getPosition();
        worldProvider.setBlock(pos, blockType);
    }

    @Override
    public boolean isRequestable(EntityRef block) {
        Vector3i position = block.getComponent(BlockComponent.class).getPosition(new Vector3i());
        Vector3i pos = new Vector3i();
        for (int[] neighbor : DIRECT_NEIGHBORS) {
            pos.set(position.x + neighbor[0], position.y + neighbor[1], position.z + neighbor[2]);
            Block solid = worldProvider.getBlock(pos);
            if (!solid.isPenetrable()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public float cooldownTime() {
        return 1;
    }

    @Override
    public String toString() {
        return "Build Block";
    }

    /**
     * Set the block type that this behavior will use to build
     *
     * @param blockUri The name (URI) of the block to use. Uses "CoreAssets:Dirt" by default.
     * @return True if block exists or intentionally set to null. False if block not found.
     */
    public boolean setBlock(String blockUri) {
        Block tempBlock = blockManager.getBlock(blockUri);
        if (tempBlock == null && blockUri != null) {
            return false;
        }
        blockType = tempBlock;
        return true;
    }

    /**
     * Set the block type that this behavior will use to build
     *
     * @param block The block to use. Uses "CoreAssets:Dirt" by default.
     */
    public void setBlock(Block block) {
        blockType = block;
    }

    /**
     * @return The block that will be placed
     */
    public Block getBlock() {
        return blockType;
    }
}

