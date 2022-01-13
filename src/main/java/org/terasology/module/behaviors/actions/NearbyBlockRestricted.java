// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.actions;

import com.google.common.collect.Lists;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.module.behaviors.components.StrayRestrictionComponent;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.world.block.BlockArea;
import org.terasology.module.behaviors.components.MinionMoveComponent;
import org.terasology.navgraph.WalkableBlock;

import java.util.List;
import java.util.Random;

/**
 * Sets a character's {@link MinionMoveComponent} target to a random nearby block inside the area defined in the
 * character's {@link StrayRestrictionComponent}.
 */
@BehaviorAction(name = "set_target_nearby_block_restricted")
public class NearbyBlockRestricted extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(NearbyBlockRestricted.class);

    /**
     * The random number provider for choosing the nearby block.
     */
    private Random random = new Random();

    /**
     * The world region that this character is allowed to stray in. Defines an x&z area in world space.
     */
    private BlockArea allowedRegion = new BlockArea(BlockArea.INVALID);

    /**
     * The probability out of 100 that this character will have a new target selected when this node is run.
     */
    private int moveProbability = 100;

    @Override
    public void construct(Actor actor) {
        if (!actor.hasComponent(StrayRestrictionComponent.class)) {
            logger.warn("Actor used behavior node set_target_nearby_block_restricted, but doesn't have a StrayRestrictionComponent!");
            return;
        }

        StrayRestrictionComponent strayRestriction = actor.getComponent(StrayRestrictionComponent.class);
        allowedRegion.set(strayRestriction.allowedRegion);
    }

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        if (random.nextInt(100) > (99 - moveProbability)) {
            MinionMoveComponent moveComponent = actor.getComponent(MinionMoveComponent.class);
            if (moveComponent.currentBlock != null) {
                WalkableBlock target = randomNearbyBlockRestricted(moveComponent.currentBlock);
                moveComponent.target = new Vector3f(target.getBlockPosition());
                actor.save(moveComponent);
            } else {
                return BehaviorState.FAILURE;
            }
        }
        return BehaviorState.SUCCESS;
    }

    /**
     * Finds a block close to the character, with the condition that this block must be inside character's allowed
     * area.
     *
     * @param startBlock The block that this character is currently standing on.
     * @return A random nearby block nearby inside the allowed area.
     */
    private WalkableBlock randomNearbyBlockRestricted(WalkableBlock startBlock) {
        WalkableBlock currentBlock = startBlock;
        for (int i = 0; i < random.nextInt(10) + 3; i++) {
            WalkableBlock[] neighbors = currentBlock.neighbors;
            List<WalkableBlock> existingNeighbors = Lists.newArrayList();

            for (WalkableBlock neighbor : neighbors) {
                if (allowedRegion == null || (neighbor != null && allowedRegion.contains(neighbor.x(), neighbor.z()))) {
                    existingNeighbors.add(neighbor);
                }
            }
            if (existingNeighbors.size() > 0) {
                currentBlock = existingNeighbors.get(random.nextInt(existingNeighbors.size()));
            }
        }
        logger.debug("Looking for a block: my block is {}, found destination {}", startBlock.getBlockPosition(), currentBlock.getBlockPosition());
        return currentBlock;
    }

}
