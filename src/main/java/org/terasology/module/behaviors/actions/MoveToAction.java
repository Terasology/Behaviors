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
import org.terasology.engine.logic.characters.CharacterMovementComponent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.WorldProvider;
import org.terasology.engine.world.block.Blocks;
import org.terasology.module.behaviors.components.MinionMoveComponent;
import org.terasology.module.behaviors.plugin.MovementPlugin;
import org.terasology.module.behaviors.plugin.WalkingMovementPlugin;
import org.terasology.module.behaviors.systems.PluginSystem;

/**
 * Uses an actor's MovementPlugin to move it to FlexibleMovementComponent.target
 * <p>
 * SUCCESS: When the actor reaches FlexibleMovementComponent.target
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
        MinionMoveComponent flexibleMovementComponent = actor.getComponent(MinionMoveComponent.class);
        flexibleMovementComponent.sequenceNumber = 0;
        actor.save(flexibleMovementComponent);
    }

    @Override
    public BehaviorState modify(Actor actor, BehaviorState prevResult) {
        LocationComponent location = actor.getComponent(LocationComponent.class);
        MinionMoveComponent flexibleMovementComponent = actor.getComponent(MinionMoveComponent.class);
        CharacterMovementComponent characterMovementComponent = actor.getComponent(CharacterMovementComponent.class);

        // we need to translate the movement target to an expected real world position
        // in practice we just need to adjust the Y so that it's resting on top of the block at the right height
        Vector3f adjustedMoveTarget = new Vector3f(flexibleMovementComponent.target);

        // this is the result of experimentation and some penwork
        //    float adjustedY = (float) Math.ceil(adjustedMoveTarget.y - halfHeight) + halfHeight - 0.5f;
        //      adjustedMoveTarget.setY(adjustedY);

        Vector3f position = location.getWorldPosition(new Vector3f());
        if (Blocks.toBlockPos(position).equals(flexibleMovementComponent.target)) {
            return BehaviorState.SUCCESS;
        }
        // Cannot find path too long;
        if (flexibleMovementComponent.sequenceNumber > 200) {
            flexibleMovementComponent.resetPath();
            actor.save(flexibleMovementComponent);
            return BehaviorState.FAILURE;
        }

        flexibleMovementComponent.sequenceNumber++;
        MovementPlugin plugin = pluginSystem.getMovementPlugin(actor.getEntity());
        CharacterMoveInputEvent result = plugin.move(
                actor.getEntity(),
                adjustedMoveTarget,
                flexibleMovementComponent.sequenceNumber
        );

        if (result == null) {
            // this is ugly, but due to unknown idiosyncrasies in the engine character movement code, characters
            // sometimes sink into solid blocks below them. This causes reachability checks to fail intermittently,
            // especially when characters stop moving. In an ideal world, we'd exit failure here to indicate our
            // path is no longer valid. However, we instead fall back to a default movement plugin in the hopes
            // that a gentle nudge in a probably-correct direction will at least make the physics reconcile the
            // intersection, and hopefully return to properly penetrable blocks.
            logger.debug("Movement plugin returned null");
            MovementPlugin fallbackPlugin = new WalkingMovementPlugin(world, time);
            result = fallbackPlugin.move(
                    actor.getEntity(),
                    adjustedMoveTarget,
                    flexibleMovementComponent.sequenceNumber
            );
        }

        actor.getEntity().send(result);
        flexibleMovementComponent.lastInput = time.getGameTimeInMs();
        flexibleMovementComponent.collidedHorizontally = false;
        actor.save(flexibleMovementComponent);

        return BehaviorState.RUNNING;
    }
}
