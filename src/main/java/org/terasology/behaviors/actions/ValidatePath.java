// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.actions;

import org.joml.Vector3i;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.registry.In;
import org.terasology.behaviors.components.FlexibleMovementComponent;
import org.terasology.behaviors.system.FlexibleMovementSystem;
import org.terasology.behaviors.system.PluginSystem;
import org.terasology.flexiblepathfinding.PathfinderSystem;
import org.terasology.flexiblepathfinding.plugins.JPSPlugin;

import java.util.List;

/**
 * Validates the entity's current path for walkability (according to the pathfinding plugin its using)
 * <p>
 * SUCCESS: when there are no unwalkable waypoints
 * <p>
 * FAILURE: otherwise
 */
@BehaviorAction(name = "validate_path")
public class ValidatePath extends BaseAction {

    BehaviorState pathStatus = null;
    List<Vector3i> pathResult = null;
    @In
    private PathfinderSystem system;
    @In
    private PluginSystem pluginSystem;
    @In
    private FlexibleMovementSystem flexibleMovementSystem;

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {

        FlexibleMovementComponent flexibleMovementComponent = actor.getComponent(FlexibleMovementComponent.class);
        JPSPlugin pathfindingPlugin = pluginSystem.getMovementPlugin(actor.getEntity()).getJpsPlugin(actor.getEntity());
        if (flexibleMovementComponent == null || pathfindingPlugin == null) {
            return BehaviorState.FAILURE;
        }
//            for(Vector3i pos : actor().getComponent(FlexibleMovementComponent.class).getPath()) {
//                if(!pathfindingPlugin.isWalkable(pos)) {
//                    return Status.FAILURE;
//                }
//            }
        return BehaviorState.SUCCESS;
    }
}
