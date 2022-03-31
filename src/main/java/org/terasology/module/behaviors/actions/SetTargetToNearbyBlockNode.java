// Copyright 2022 The Terasology Foundation
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
import org.terasology.engine.registry.CoreRegistry;
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
    public void construct(Actor actor) {
        // TODO: Temporary fix for injection malfunction in actions, remove as soon as injection malfunction in actions is fixed.
        if (movementPluginSystem == null) {
            movementPluginSystem = CoreRegistry.get(PluginSystem.class);
        }
    }

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        logger.debug("Actor {}: in set_target_nearby_block Action", actor.getEntity().getId());
        if (random.nextInt(100) > (99 - moveProbability)) {
            MinionMoveComponent moveComponent = actor.getComponent(MinionMoveComponent.class);
            LocationComponent locationComponent = actor.getComponent(LocationComponent.class);
            JPSPlugin plugin = movementPluginSystem.getMovementPlugin(actor.getEntity())
                    .getJpsPlugin(actor.getEntity());
            if (locationComponent != null) {
                Vector3i startBlock = Blocks.toBlockPos(locationComponent.getWorldPosition(new Vector3f()));
                logger.debug("... [{}]  start position: {}", actor.getEntity().getId(), startBlock);
                Vector3ic target = findRandomNearbyBlock(startBlock, plugin);
                if (!plugin.isWalkable(target)) {
                    logger.debug("... [{}] target position: {} not walkable", actor.getEntity().getId(), target);
                    return BehaviorState.FAILURE;
                }
                moveComponent.target.set(target);
                moveComponent.setPathGoal(new Vector3i(target));
                actor.save(moveComponent);
                logger.debug("... [{}] target position: {} - distance: {}", actor.getEntity().getId(), target, target.sub(startBlock, new Vector3i()).length());
            } else {
                logger.debug("... [{}] failed", actor.getEntity().getId());
                return BehaviorState.FAILURE;
            }
        }
        return BehaviorState.SUCCESS;
    }

    /**
     * TODO: make sure the block is on the ground, i.e., the actor is able to stay at at that location
     *       obviously, this depends on the entities movement modes, a flying entity can obviously just hover a round...
     *       => the question now is how a plugin can contribute to the path, but should be excluded from finding a target ...
     *
     * TODO: can this be solved using different sets of plugins?
     *          movementTypes: ["walking", "leaping"]
     *          idleTypes: ["walking"]
     *       ideally, we can instantiate all plugins once an re-use them in different contexts (but that might prove difficult due to
     *       concurrency when building up caches?)
     *
     * @param startBlock
     * @param plugin
     * @return
     */
    private Vector3ic findRandomNearbyBlock(Vector3ic startBlock, JPSPlugin plugin) {
        Vector3i currentBlock = new Vector3i(startBlock);
        long currentDistance = 0;
        // general distance to startBlock is defined by number of steps (currently at least 3, at max 13)
        for (int i = 0; i < random.nextInt(10) + 3; i++) {
            BlockRegionc neighbors = new BlockRegion(currentBlock).expand(1, 1, 1);
            Vector3ic[] allowedBlocks = Iterators.toArray(
                    Iterators.transform(Iterators.filter(neighbors.iterator(),
                                    (candidate) -> candidate.distanceSquared(startBlock) > currentDistance
                                            && plugin.isReachable(currentBlock, candidate)),
                            Vector3i::new),
                    Vector3ic.class);
            if (allowedBlocks.length > 0) {
                currentBlock.set(allowedBlocks[random.nextInt(allowedBlocks.length)]);
            }
        }

        // TODO: keep history of steps and trace back in case final bock is not walkable
        return currentBlock;
    }

}
