// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion.work.systems;

import com.google.common.collect.Lists;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.engine.core.SimpleUri;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.ComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.WorldProvider;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockComponent;
import org.terasology.minion.work.Work;
import org.terasology.minion.work.WorkFactory;
import org.terasology.minion.work.WorkTargetComponent;
import org.terasology.navgraph.WalkableBlock;
import org.terasology.pathfinding.componentSystem.PathfinderSystem;

import java.util.List;

/**
 *
 */
@RegisterSystem
public class RemoveBlock extends BaseComponentSystem implements Work, ComponentSystem {
    private static final int[][] NEIGHBORS = new int[][]{
            {-1, 0, 0}, {1, 0, 0}, {0, 0, -1}, {0, 0, 1},
//            {-1, 1, 0}, {1, 1, 0}, {0, 1, -1}, {0, 1, 1},
            {-1, -1, 0}, {1, -1, 0}, {0, -1, -1}, {0, -1, 1},
            {-1, -2, 0}, {1, -2, 0}, {0, -2, -1}, {0, -2, 1},
    };

    @In
    private PathfinderSystem pathfinderSystem;
    @In
    private WorldProvider worldProvider;
    @In
    private WorkFactory workFactory;

    private final SimpleUri uri;




    public RemoveBlock() {
        uri = new SimpleUri("Behaviors:removeBlock");
    }

    @Override
    public void initialise() {
        workFactory.register(this);

    }

    @Override
    public void shutdown() {
    }

    @Override
    public SimpleUri getUri() {
        return uri;
    }

    @Override
    public List<WalkableBlock> getTargetPositions(EntityRef block) {
        List<WalkableBlock> result = Lists.newArrayList();

        Vector3i worldPos = block.getComponent(BlockComponent.class).getPosition(new Vector3i());
        WalkableBlock walkableBlock;
        Vector3i pos = new Vector3i();
        for (int[] neighbor : NEIGHBORS) {
            pos.set(worldPos.x + neighbor[0], worldPos.y + neighbor[1], worldPos.z + neighbor[2]);
            walkableBlock = pathfinderSystem.getBlock(pos);
            if (walkableBlock != null) {
                result.add(walkableBlock);
            }
        }
        return result;
    }

    @Override
    public boolean canMinionWork(EntityRef block, EntityRef minion) {
        Vector3f pos = block.getComponent(LocationComponent.class).getWorldPosition(new Vector3f())
            .sub(minion.getComponent(LocationComponent.class).getWorldPosition(new Vector3f()));
        pos.y /= 4;
        float length = pos.length();
        return length < 2;
    }

    @Override
    public void letMinionWork(EntityRef block, EntityRef minion) {
        block.removeComponent(WorkTargetComponent.class);
        Vector3ic pos = block.getComponent(BlockComponent.class).getPosition();
        worldProvider.setBlock(pos, workFactory.getAir());
    }

    @Override
    public boolean isAssignable(EntityRef block) {
        Vector3ic pos = block.getComponent(BlockComponent.class).getPosition();
        Block type = worldProvider.getBlock(pos);
        return !type.isPenetrable();
    }

    @Override
    public boolean isRequestable(EntityRef block) {
        return true;
    }

    @Override
    public float cooldownTime() {
        return 0;
    }

    @Override
    public String toString() {
        return "Remove Block";
    }
}



