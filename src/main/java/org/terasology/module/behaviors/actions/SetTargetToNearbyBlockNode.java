// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.actions;

import com.google.common.collect.Lists;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.registry.In;
import org.terasology.module.behaviors.components.MinionMoveComponent;
import org.terasology.navgraph.WalkableBlock;
import org.terasology.pathfinding.componentSystem.PathfinderSystem;

import java.util.List;
import java.util.Random;


@BehaviorAction(name = "set_target_nearby_block")
public class SetTargetToNearbyBlockNode extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(SetTargetToNearbyBlockNode.class);
    private int moveProbability = 100;
    private transient Random random = new Random();
    @In
    private PathfinderSystem pathfinderSystem;

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        if (random.nextInt(100) > (99 - moveProbability)) {
            MinionMoveComponent moveComponent = actor.getComponent(MinionMoveComponent.class);
            if (moveComponent.currentBlock != null) {
                WalkableBlock target = findRandomNearbyBlock(moveComponent.currentBlock);
                moveComponent.target = new Vector3f(target.getBlockPosition());
                actor.save(moveComponent);
            } else {
                return BehaviorState.FAILURE;
            }
        }
        return BehaviorState.SUCCESS;
    }

    private WalkableBlock findRandomNearbyBlock(WalkableBlock startBlock) {
        WalkableBlock currentBlock = startBlock;
        for (int i = 0; i < random.nextInt(10) + 3; i++) {
            WalkableBlock[] neighbors = currentBlock.neighbors;
            List<WalkableBlock> existingNeighbors = Lists.newArrayList();
            for (WalkableBlock neighbor : neighbors) {
                if (neighbor != null) {
                    existingNeighbors.add(neighbor);
                }
            }
            if (existingNeighbors.size() > 0) {
                currentBlock = existingNeighbors.get(random.nextInt(existingNeighbors.size()));
            }
        }
        logger.debug(String.format("Looking for a block: my block is %s, found destination %s", 
            startBlock.getBlockPosition(), currentBlock.getBlockPosition()));
        return currentBlock;
    }

}
