// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblemovement.actions;

import com.google.common.collect.Iterators;
import org.joml.RoundingMode;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.behaviors.components.FleeingComponent;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.engine.world.block.Blocks;
import org.terasology.flexiblemovement.FlexibleMovementComponent;
import org.terasology.flexiblemovement.system.PluginSystem;
import org.terasology.flexiblepathfinding.plugins.JPSPlugin;

import java.util.Arrays;
import java.util.Random;

import static java.lang.Integer.min;

@BehaviorAction(name = "flex_set_target_nearby_block_away_from_instigator")
public class SetTargetToNearbyBlockAwayFromInstigatorAction extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(SetTargetToNearbyBlockAwayFromInstigatorAction.class);
    private static final int RANDOM_BLOCK_ITERATIONS = 10;
    private final Random random = new Random();
    @In
    private PluginSystem movementPluginSystem;

    @Override
    public BehaviorState modify(Actor actor, BehaviorState state) {
        FlexibleMovementComponent moveComponent = actor.getComponent(FlexibleMovementComponent.class);
        LocationComponent locationComponent = actor.getComponent(LocationComponent.class);

        if (locationComponent != null && moveComponent != null) {
            Vector3i currentBlock = Blocks.toBlockPos(locationComponent.getWorldPosition(new Vector3f()));

            moveComponent.target = findRandomNearbyBlockAwayFromPlayer(currentBlock, actor);
            actor.save(moveComponent);
        } else {
            return BehaviorState.FAILURE;
        }
        return BehaviorState.SUCCESS;
    }

    private Vector3i findRandomNearbyBlockAwayFromPlayer(Vector3ic startBlock, Actor actor) {
        Vector3i currentBlock = new Vector3i(startBlock);
        FleeingComponent fleeingComponent = actor.getComponent(FleeingComponent.class);
        Vector3i playerPosition = new Vector3i(
                fleeingComponent.instigator.getComponent(LocationComponent.class).getWorldPosition(new Vector3f()),
                RoundingMode.FLOOR);
        JPSPlugin plugin = movementPluginSystem.getMovementPlugin(actor.getEntity())
                .getJpsPlugin(actor.getEntity());

        for (int i = 0; i < RANDOM_BLOCK_ITERATIONS; i++) {
            BlockRegion region = new BlockRegion(currentBlock);
            Vector3ic[] allowedBlocks = Iterators.toArray(
                    Iterators.transform(Iterators.filter(region.expand(1, 1, 1).iterator(),
                                    (candidate) -> plugin.isReachable(currentBlock, candidate)),
                            Vector3i::new),
                    Vector3ic.class);

            if (allowedBlocks.length > 0) {
                Arrays.sort(allowedBlocks, (one, two) -> {
                    double a = one.distanceSquared(playerPosition);
                    double b = two.distanceSquared(playerPosition);
                    return Double.compare(b, a);
                });
                currentBlock.set(allowedBlocks[random.nextInt(min(4, allowedBlocks.length))]);
            }
        }
        return currentBlock;
    }

}
