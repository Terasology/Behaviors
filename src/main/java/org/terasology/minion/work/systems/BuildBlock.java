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
package org.terasology.minion.work.systems;

import com.google.common.collect.Lists;
import org.terasology.engine.SimpleUri;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.ComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.math.geom.Vector3i;
import org.terasology.minion.work.Work;
import org.terasology.minion.work.WorkFactory;
import org.terasology.minion.work.WorkTargetComponent;
import org.terasology.navgraph.WalkableBlock;
import org.terasology.pathfinding.componentSystem.PathfinderSystem;
import org.terasology.registry.In;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.BlockManager;

import java.util.List;

/**
 * @author synopia
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
        uri = new SimpleUri("Pathfinding:buildBlock");
    }

    @Override
    public void initialise() {
        workFactory.register(this);
        blockType = blockManager.getBlock("core:Dirt");
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
        if (block == null || !block.hasComponent(BlockComponent.class)) {
            return targetPositions;
        }
        Vector3i position = new Vector3i(block.getComponent(BlockComponent.class).getPosition());
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
        Vector3i position = new Vector3i(block.getComponent(BlockComponent.class).getPosition());
        position.y--;
        WalkableBlock expectedBlock = pathfinderSystem.getBlock(position);
        return actualBlock == expectedBlock;
    }

    @Override
    public boolean isAssignable(EntityRef block) {
        Vector3i position = new Vector3i(block.getComponent(BlockComponent.class).getPosition());
        Block type = worldProvider.getBlock(position);
        return type.isPenetrable();
    }

    @Override
    public void letMinionWork(EntityRef block, EntityRef minion) {
        block.removeComponent(WorkTargetComponent.class);
        worldProvider.setBlock(block.getComponent(BlockComponent.class).getPosition(), blockType);
    }

    @Override
    public boolean isRequestable(EntityRef block) {
        Vector3i position = new Vector3i(block.getComponent(BlockComponent.class).getPosition());
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
}
