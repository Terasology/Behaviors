// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion.work.systems;

import com.google.common.collect.Lists;
import org.joml.Vector3i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.SimpleUri;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.ComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.minion.work.Work;
import org.terasology.minion.work.WorkFactory;
import org.terasology.minion.work.WorkTargetComponent;
import org.terasology.navgraph.WalkableBlock;
import org.terasology.pathfinding.componentSystem.PathfinderSystem;
import org.terasology.registry.In;
import org.terasology.world.block.BlockComponent;

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
        WalkableBlock walkableBlock = pathfinderSystem.getBlock(block.getComponent(BlockComponent.class).getPosition(new Vector3i()));
        if (walkableBlock != null) {
            targetPositions.add(walkableBlock);
        }

        return targetPositions;
    }

    @Override
    public boolean canMinionWork(EntityRef block, EntityRef minion) {
        WalkableBlock actualBlock = pathfinderSystem.getBlock(minion);
        WalkableBlock expectedBlock = pathfinderSystem.getBlock(block.getComponent(BlockComponent.class).getPosition(new Vector3i()));
        logger.info("{} - {}", actualBlock.getBlockPosition(), expectedBlock.getBlockPosition());
        return actualBlock == expectedBlock;
    }

    @Override
    public boolean isAssignable(EntityRef block) {
        if (block == null || !block.hasComponent(BlockComponent.class)) {
            return false;
        }
        WalkableBlock walkableBlock = pathfinderSystem.getBlock(block.getComponent(BlockComponent.class).getPosition(new Vector3i()));
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


