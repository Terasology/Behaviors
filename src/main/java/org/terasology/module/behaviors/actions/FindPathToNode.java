// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.actions;

import org.joml.Vector3f;
import org.joml.Vector3ic;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.block.Blocks;
import org.terasology.flexiblepathfinding.JPSConfig;
import org.terasology.flexiblepathfinding.PathfinderSystem;
import org.terasology.module.behaviors.components.MinionMoveComponent;
import org.terasology.module.behaviors.systems.PluginSystem;

/**
 * Finds a path to the pathGoalPosition of the Actor, stores it in FlexibileMovementComponent.path
 * <p/>
 * SUCCESS: When the pathfinder returns a valid path
 * <p/>
 * FAILURE: When the pathfinder returns a failure or invalid path
 *
 */
@BehaviorAction(name = "find_path")
public class FindPathToNode extends BaseAction {

    @In
    PathfinderSystem pathfinderSystem;

    @In
    PluginSystem pluginSystem;


    @Override
    public void construct(Actor actor) {
        if (pathfinderSystem == null) {
            pathfinderSystem = CoreRegistry.get(PathfinderSystem.class);
        }
        if (pluginSystem == null) {
            pluginSystem = CoreRegistry.get(PluginSystem.class);
        }

        MinionMoveComponent minionMoveComponent = actor.getComponent(MinionMoveComponent.class);
        minionMoveComponent.running = true;
        Vector3ic start = Blocks.toBlockPos(actor.getComponent(LocationComponent.class).getWorldPosition(new Vector3f()));
        Vector3ic goal = actor.getComponent(MinionMoveComponent.class).getPathGoal();

        JPSConfig config = new JPSConfig(start, goal);
        config.useLineOfSight = false;
        config.requester = actor.getEntity();
        config.maxTime = 10f;
        config.maxDepth = 150;
        config.goalDistance = minionMoveComponent.pathGoalDistance;
        config.plugin = pluginSystem.getMovementPlugin(actor.getEntity()).getJpsPlugin(actor.getEntity());

        int id = pathfinderSystem.requestPath(config, (path, target) -> {
            MinionMoveComponent minionMoveComponent1 = actor.getComponent(MinionMoveComponent.class);
            minionMoveComponent1.running = false;
            if (path == null || path.size() == 0) {
                actor.save(minionMoveComponent1);
                return;
            }
            path.remove(0);

            minionMoveComponent1.setPath(path);

            actor.save(minionMoveComponent1);
        });
    }

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        // this an action node, so it will always be called with BehaviorState.UNDEFINED
        if (result == BehaviorState.RUNNING) {
            // this can never happen o.O
            return result;
        }
        MinionMoveComponent minionMoveComponent = actor.getComponent(MinionMoveComponent.class);
        if (minionMoveComponent.running) {
            return BehaviorState.RUNNING;
        }
        return minionMoveComponent.getPath().isEmpty() ? BehaviorState.FAILURE : BehaviorState.SUCCESS;
    }
}
