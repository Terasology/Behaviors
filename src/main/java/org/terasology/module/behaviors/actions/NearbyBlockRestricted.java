// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.actions;

import com.google.common.collect.Iterators;
import org.joml.Math;
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
import org.terasology.engine.world.block.BlockArea;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.engine.world.block.BlockRegionc;
import org.terasology.engine.world.block.Blocks;
import org.terasology.flexiblepathfinding.plugins.JPSPlugin;
import org.terasology.module.behaviors.components.MinionMoveComponent;
import org.terasology.module.behaviors.components.StrayRestrictionComponent;
import org.terasology.module.behaviors.systems.PluginSystem;

import java.util.Optional;
import java.util.Random;

/**
 * Sets a character's {@link MinionMoveComponent} target to a random nearby block inside the area defined in the character's {@link
 * StrayRestrictionComponent}.
 */
@BehaviorAction(name = "set_target_nearby_block_restricted")
public class NearbyBlockRestricted extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(NearbyBlockRestricted.class);
    /**
     * The random number provider for choosing the nearby block.
     */
    private final Random random = new Random();
    /**
     * The world region that this character is allowed to stray in. Defines an x&z area in world space.
     */
    private final BlockArea allowedRegion = new BlockArea(BlockArea.INVALID);
    @In
    private PluginSystem movementPluginSystem;
    /**
     * The probability out of 100 that this character will have a new target selected when this node is run.
     */
    private int moveProbability = 100;

    @Override
    public void construct(Actor actor) {
        // TODO: Temporary fix for injection malfunction in actions, remove as soon as injection malfunction in actions is fixed.
        if (movementPluginSystem == null) {
            movementPluginSystem = CoreRegistry.get(PluginSystem.class);
        }

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
            LocationComponent locationComponent = actor.getComponent(LocationComponent.class);
            JPSPlugin plugin = movementPluginSystem.getMovementPlugin(actor.getEntity())
                    .getJpsPlugin(actor.getEntity());

            if (moveComponent != null && locationComponent != null) {
                Vector3f currentBlock = locationComponent.getWorldPosition(new Vector3f());
                moveComponent.target = randomNearbyBlockRestricted(currentBlock, plugin);
                actor.save(moveComponent);
            } else {
                return BehaviorState.FAILURE;
            }
        }
        return BehaviorState.SUCCESS;
    }

    /**
     * Finds a block close to the character, with the condition that this block must be inside character's allowed area.
     *
     * @param startBlock The block that this character is currently standing on.
     * @param plugin character moving plugin
     * @return A random nearby block nearby inside the allowed area.
     */
    private Vector3i randomNearbyBlockRestricted(Vector3f startBlock, JPSPlugin plugin) {
        Vector3i currentBlock = Blocks.toBlockPos(startBlock);
        for (int i = 0; i < random.nextInt(10) + 3; i++) {
            BlockRegionc neighbors = new BlockRegion(currentBlock).expand(1, 1, 1);
            Optional<BlockRegionc> allowed = getAllowedRegionWith(neighbors);
            if (allowed.isPresent()) {
                BlockRegionc blockRegionc = allowed.get();
                Vector3ic[] allowedBlocks = Iterators.toArray(
                        Iterators.transform(Iterators.filter(blockRegionc.iterator(),
                                        (candidate) -> plugin.isReachable(currentBlock, candidate)),
                                Vector3i::new),
                        Vector3ic.class);
                if (allowedBlocks.length > 0) {
                    currentBlock.set(allowedBlocks[random.nextInt(allowedBlocks.length)]);
                }
            }
        }
        return currentBlock;
    }

    private Optional<BlockRegionc> getAllowedRegionWith(BlockRegionc dest) {
        if (!allowedRegion.isValid()) {
            return Optional.of(dest);
        }

        BlockRegion blockRegion = new BlockRegion(
                Math.max(allowedRegion.minX(), dest.minX()),
                Math.max(allowedRegion.minY(), dest.minY()),
                dest.minZ(),
                Math.min(allowedRegion.maxX(), dest.maxX()),
                Math.min(allowedRegion.maxY(), dest.maxY()),
                dest.maxZ());
        if (blockRegion.isValid()) {
            return Optional.of(blockRegion);
        } else {
            return Optional.empty();
        }
    }
}
