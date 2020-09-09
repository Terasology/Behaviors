// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion.work.systems;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.core.SimpleUri;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.ComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.block.BlockComponent;
import org.terasology.math.geom.Vector3i;
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
public class WalkToBlock extends BaseComponentSystem implements Work, ComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(WalkToBlock.class);
    private final SimpleUri uri;
    @In
    private PathfinderSystem pathfinderSystem;
    @In
    private WorkFactory workFactory;

    public WalkToBlock() {
        uri = new SimpleUri("Behaviors:walkToBlock");
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

    public List<WalkableBlock> getTargetPositions(EntityRef block) {
        List<WalkableBlock> targetPositions = Lists.newArrayList();
        if (block == null || !block.hasComponent(BlockComponent.class)) {
            return targetPositions;
        }
        Vector3i position = block.getComponent(BlockComponent.class).getPosition();
        WalkableBlock walkableBlock = pathfinderSystem.getBlock(position);
        if (walkableBlock != null) {
            targetPositions.add(walkableBlock);
        }

        return targetPositions;
    }

    @Override
    public boolean canMinionWork(EntityRef block, EntityRef minion) {
        WalkableBlock actualBlock = pathfinderSystem.getBlock(minion);
        WalkableBlock expectedBlock = pathfinderSystem.getBlock(block.getComponent(BlockComponent.class).getPosition());
        logger.info("{} - {}", actualBlock.getBlockPosition(), expectedBlock.getBlockPosition());
        return actualBlock == expectedBlock;
    }

    @Override
    public boolean isAssignable(EntityRef block) {
        if (block == null || !block.hasComponent(BlockComponent.class)) {
            return false;
        }
        WalkableBlock walkableBlock = pathfinderSystem.getBlock(block.getComponent(BlockComponent.class).getPosition());
        return walkableBlock != null;
    }

    @Override
    public void letMinionWork(EntityRef block, EntityRef minion) {
        block.removeComponent(WorkTargetComponent.class);
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
        return "Walk To Block";
    }
}


