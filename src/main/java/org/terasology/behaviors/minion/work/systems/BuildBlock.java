// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.minion.work.systems;

import com.google.common.collect.Lists;
import org.terasology.behaviors.minion.work.Work;
import org.terasology.behaviors.minion.work.WorkFactory;
import org.terasology.behaviors.minion.work.WorkTargetComponent;
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
import org.terasology.math.geom.Vector3i;
import org.terasology.pathfinding.componentSystem.PathfinderSystem;
import org.terasology.pathfinding.navgraph.WalkableBlock;

import java.util.List;

/**
 *
 */
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

    public List<WalkableBlock> getTargetPositions(EntityRef block) {
        List<WalkableBlock> targetPositions = Lists.newArrayList();
        if (block == null || !block.hasComponent(BlockComponent.class) || blockType == null) {
            return targetPositions;
        }
        Vector3i position = new Vector3i(block.getComponent(BlockComponent.class).position);
        position.y--;
        WalkableBlock walkableBlock = pathfinderSystem.getBlock(position);
        if (walkableBlock != null) {
            targetPositions.add(walkableBlock);
        }

        return targetPositions;
    }

    @Override
    public boolean canMinionWork(EntityRef block, EntityRef minion) {
        WalkableBlock actualBlock = pathfinderSystem.getBlock(minion);
        Vector3i position = new Vector3i(block.getComponent(BlockComponent.class).position);
        position.y--;
        WalkableBlock expectedBlock = pathfinderSystem.getBlock(position);
        return actualBlock == expectedBlock && blockType != null;
    }

    @Override
    public boolean isAssignable(EntityRef block) {
        Vector3i position = new Vector3i(block.getComponent(BlockComponent.class).position);
        Block type = worldProvider.getBlock(position);
        return type.isPenetrable() && blockType != null;
    }

    @Override
    public void letMinionWork(EntityRef block, EntityRef minion) {
        block.removeComponent(WorkTargetComponent.class);
        worldProvider.setBlock(block.getComponent(BlockComponent.class).position, blockType);
    }

    @Override
    public boolean isRequestable(EntityRef block) {
        Vector3i position = new Vector3i(block.getComponent(BlockComponent.class).position);
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
     * @param uri The name of the block to use. Uses "CoreAssets:Dirt" by default.
     * @return True if block exists or intentionally set to null. False if block not found.
     */
    public boolean setBlock(String uri) {
        Block tempBlock = blockManager.getBlock(uri);
        if (tempBlock == null && uri != null) return false;
        blockType = tempBlock;
        return true;
    }

    /**
     * @return The block that will be placed
     */
    public Block getBlock() {
        return blockType;
    }

    /**
     * Set the block type that this behavior will use to build
     *
     * @param block The block to use. Uses "CoreAssets:Dirt" by default.
     */
    public void setBlock(Block block) {
        blockType = block;
    }
}

