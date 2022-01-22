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
 * Finds a path to the pathGoalPosition of the Actor, stores it in {@link MinionMoveComponent#getPath()}.
 * <p/>
 * SUCCESS: When the pathfinder returns a valid path
 * <p/>
 * FAILURE: When the pathfinder returns a failure or invalid path
 *
 */
@BehaviorAction(name = "find_path")
public class FindPathToNode extends BaseAction {

    @In
    transient PathfinderSystem pathfinderSystem;

    @In
    transient PluginSystem pluginSystem;

    // TODO: how do we want to remember state in actions?
    // 1. action field as used here
    // 2. actor.setValue, actor.getValue as used in SleepAction
    // 3. write to, read from component
    // 4. ...
    private boolean pathfindingFinished = false;


    @Override
    public void construct(Actor actor) {
        if (pathfinderSystem == null) {
            pathfinderSystem = CoreRegistry.get(PathfinderSystem.class);
        }
        if (pluginSystem == null) {
            pluginSystem = CoreRegistry.get(PluginSystem.class);
        }

        MinionMoveComponent minionMoveComponent = actor.getComponent(MinionMoveComponent.class);
        Vector3ic start = Blocks.toBlockPos(actor.getComponent(LocationComponent.class).getWorldPosition(new Vector3f()));
        Vector3ic goal = actor.getComponent(MinionMoveComponent.class).getPathGoal();

        JPSConfig config = new JPSConfig(start, goal);
        config.useLineOfSight = false;
        config.requester = actor.getEntity();
        config.maxTime = 10f;
        config.maxDepth = 150;
        config.goalDistance = minionMoveComponent.goalTolerance;
        config.plugin = pluginSystem.getMovementPlugin(actor.getEntity()).getJpsPlugin(actor.getEntity());

        int id = pathfinderSystem.requestPath(config, (path, target) -> {
            if (path == null || path.size() == 0) {
                pathfindingFinished = true;
                return;
            }
            path.remove(0);

            MinionMoveComponent minionMoveComponent1 = actor.getComponent(MinionMoveComponent.class);
            minionMoveComponent1.setPath(path);
            actor.save(minionMoveComponent1);
            pathfindingFinished = true;
        });
    }

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        // this an action node, so it will always be called with BehaviorState.UNDEFINED
        if (result == BehaviorState.RUNNING) {
            // this can never happen o.O
            return result;
        }
        if (!pathfindingFinished) {
            return BehaviorState.RUNNING;
        }
        MinionMoveComponent minionMoveComponent = actor.getComponent(MinionMoveComponent.class);
        return minionMoveComponent.getPath().isEmpty() ? BehaviorState.FAILURE : BehaviorState.SUCCESS;
    }
}
