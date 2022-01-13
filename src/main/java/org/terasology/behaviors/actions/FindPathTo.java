// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.actions;

import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.block.Blocks;
import org.terasology.behaviors.components.FlexibleMovementComponent;
import org.terasology.behaviors.system.PluginSystem;
import org.terasology.flexiblepathfinding.JPSConfig;
import org.terasology.flexiblepathfinding.PathfinderCallback;
import org.terasology.flexiblepathfinding.PathfinderSystem;

import java.util.List;

/**
 * Finds a path to the pathGoalPosition of the Actor, stores it in FlexibileMovementComponent.path
 * <p/>
 * SUCCESS: When the pathfinder returns a valid path
 * <p/>
 * FAILURE: When the pathfinder returns a failure or invalid path
 *
 */
@BehaviorAction(name = "flex_find_path")
public class FindPathTo extends BaseAction {

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

        FlexibleMovementComponent flexibleMovementComponent = actor.getComponent(FlexibleMovementComponent.class);
        Vector3ic start = Blocks.toBlockPos(actor.getComponent(LocationComponent.class).getWorldPosition(new Vector3f()));
        Vector3ic goal = actor.getComponent(FlexibleMovementComponent.class).getPathGoal();

        JPSConfig config = new JPSConfig(start, goal);
        config.useLineOfSight = false;
        config.requester = actor.getEntity();
        config.maxTime = 10f;
        config.maxDepth = 150;
        config.goalDistance = flexibleMovementComponent.pathGoalDistance;
        config.plugin = pluginSystem.getMovementPlugin(actor.getEntity()).getJpsPlugin(actor.getEntity());

        int id = pathfinderSystem.requestPath(config, new PathfinderCallback() {
            @Override
            public void pathReady(List<Vector3i> path, Vector3i target) {
                if (path == null || path.size() == 0) {
                    return;
                }
                path.remove(0);
                FlexibleMovementComponent flexibleMovementComponent = actor.getComponent(FlexibleMovementComponent.class);
                flexibleMovementComponent.setPath(path);
                actor.save(flexibleMovementComponent);
            }
        });
    }

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        if (result == BehaviorState.RUNNING) {
            return result;
        }
        FlexibleMovementComponent flexibleMovementComponent = actor.getComponent(FlexibleMovementComponent.class);
        return flexibleMovementComponent.getPath().isEmpty() ? BehaviorState.FAILURE : BehaviorState.SUCCESS;
    }
}
