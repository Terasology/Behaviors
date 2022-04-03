// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.actions;

import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.core.Time;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.logic.characters.CharacterMoveInputEvent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.WorldProvider;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.joml.geom.AABBf;
import org.terasology.module.behaviors.components.MinionMoveComponent;
import org.terasology.module.behaviors.plugin.MovementPlugin;
import org.terasology.module.behaviors.plugin.WalkingMovementPlugin;
import org.terasology.module.behaviors.systems.PluginSystem;

/**
 * Uses an actor's MovementPlugin to move it to {@link MinionMoveComponent#target}
 * <p>
 * SUCCESS: When the actor reaches {@link MinionMoveComponent#target}
 * <p>
 * FAILURE: When the actor believes it is unable to reach its immediate target
 */
@BehaviorAction(name = "move_to")
public class MoveToAction extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(MoveToAction.class);

    @In
    private Time time;
    @In
    private WorldProvider world;
    @In
    private PluginSystem pluginSystem;

    @Override
    public void construct(Actor actor) {
        if (world == null) {
            world = CoreRegistry.get(WorldProvider.class);
        }
        if (pluginSystem == null) {
            pluginSystem = CoreRegistry.get(PluginSystem.class);
        }
        MinionMoveComponent minionMoveComponent = actor.getComponent(MinionMoveComponent.class);
        minionMoveComponent.sequenceNumber = 0;
        actor.save(minionMoveComponent);
    }

    @Override
    public BehaviorState modify(Actor actor, BehaviorState prevResult) {
        logger.debug("Actor {}: in move_to Action", actor.getEntity().getId());
        LocationComponent location = actor.getComponent(LocationComponent.class);
        MinionMoveComponent minionMoveComponent = actor.getComponent(MinionMoveComponent.class);

        // we need to translate the movement target to an expected real world position
        // in practice we just need to adjust the Y so that it's resting on top of the block at the right height
        Vector3f adjustedMoveTarget = new Vector3f(minionMoveComponent.target);

        Vector3f position = location.getWorldPosition(new Vector3f());

        // Make the target area a bit smaller than the actual block to ensure that the entity
        // is well within the block boundaries before moving on.
        float tolerance = minionMoveComponent.targetTolerance;
        AABBf targetArea = new BlockRegion(minionMoveComponent.target)
                .getBounds(new AABBf())
                .expand(-tolerance, -tolerance, -tolerance);
        if (targetArea.containsPoint(position)) {
            return BehaviorState.SUCCESS;
        }
        // Could not reach target with _n_ movements - abort action;
        //
        // As we scale the movement with the tick rate (the game time delta) just counting the
        // sequence number might be a bad measure for long-running/slow movements.
        // With about 200fps this would allow for just 1 second of consecutive movement with an
        // upper bound of 200 cycles. Therefore, increasing this number to have a bit more margin.
        //
        //TODO: find a better measure for "time outs" and identifying long-running actions
        if (minionMoveComponent.sequenceNumber > 1000) {
            minionMoveComponent.resetPath();
            actor.save(minionMoveComponent);
            return BehaviorState.FAILURE;
        }

        minionMoveComponent.sequenceNumber++;
        MovementPlugin plugin = pluginSystem.getMovementPlugin(actor.getEntity());
        CharacterMoveInputEvent result = plugin.move(
                actor.getEntity(),
                adjustedMoveTarget,
                minionMoveComponent.sequenceNumber
        );

        if (result == null) {
            // this is ugly, but due to unknown idiosyncrasies in the engine character movement code, characters
            // sometimes sink into solid blocks below them. This causes reachability checks to fail intermittently,
            // especially when characters stop moving. In an ideal world, we'd exit failure here to indicate our
            // path is no longer valid. However, we instead fall back to a default movement plugin in the hopes
            // that a gentle nudge in a probably-correct direction will at least make the physics reconcile the
            // intersection, and hopefully return to properly penetrable blocks.
            logger.debug("... [{}]: Movement plugin returned null - falling back to WalkingMovementPlugin", actor.getEntity().getId());
            MovementPlugin fallbackPlugin = new WalkingMovementPlugin(world, time);
            result = fallbackPlugin.move(
                    actor.getEntity(),
                    adjustedMoveTarget,
                    minionMoveComponent.sequenceNumber
            );
        }

        actor.getEntity().send(result);
        minionMoveComponent.lastInput = time.getGameTimeInMs();
        minionMoveComponent.collidedHorizontally = false;
        actor.save(minionMoveComponent);

        return BehaviorState.RUNNING;
    }
}
