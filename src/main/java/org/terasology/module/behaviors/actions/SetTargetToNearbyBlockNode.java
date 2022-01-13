// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.actions;

import com.google.common.collect.Iterators;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.engine.world.block.BlockRegionc;
import org.terasology.engine.world.block.Blocks;
import org.terasology.flexiblepathfinding.plugins.JPSPlugin;
import org.terasology.module.behaviors.components.MinionMoveComponent;
import org.terasology.module.behaviors.systems.PluginSystem;

import java.util.Random;


@BehaviorAction(name = "set_target_nearby_block")
public class SetTargetToNearbyBlockNode extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(SetTargetToNearbyBlockNode.class);
    private int moveProbability = 100;
    private transient Random random = new Random();

    @In
    private PluginSystem movementPluginSystem;

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        if (random.nextInt(100) > (99 - moveProbability)) {
            MinionMoveComponent moveComponent = actor.getComponent(MinionMoveComponent.class);
            LocationComponent locationComponent = actor.getComponent(LocationComponent.class);
            JPSPlugin plugin = movementPluginSystem.getMovementPlugin(actor.getEntity())
                    .getJpsPlugin(actor.getEntity());
            if (locationComponent != null) {
                Vector3ic target = findRandomNearbyBlock(
                        Blocks.toBlockPos(locationComponent.getWorldPosition(new Vector3f())),
                        plugin);
                moveComponent.target.set(target);
                actor.save(moveComponent);
            } else {
                return BehaviorState.FAILURE;
            }
        }
        return BehaviorState.SUCCESS;
    }

    private Vector3ic findRandomNearbyBlock(Vector3ic startBlock, JPSPlugin plugin) {
        Vector3i currentBlock = new Vector3i(startBlock);
        for (int i = 0; i < random.nextInt(10) + 3; i++) {
            BlockRegionc neighbors = new BlockRegion(currentBlock).expand(1, 1, 1);
            Vector3ic[] allowedBlocks = Iterators.toArray(
                    Iterators.transform(Iterators.filter(neighbors.iterator(),
                                    (candidate) -> plugin.isReachable(currentBlock, candidate)),
                            Vector3i::new),
                    Vector3ic.class);
            if (allowedBlocks.length > 0) {
                currentBlock.set(allowedBlocks[random.nextInt(allowedBlocks.length)]);
            }
        }
        logger.debug(String.format("Looking for a block: my block is %s, found destination %s",
                startBlock, currentBlock));
        return currentBlock;
    }

}
