// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.actions;

import org.joml.Vector3i;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.registry.In;
import org.terasology.flexiblepathfinding.PathfinderSystem;
import org.terasology.flexiblepathfinding.plugins.JPSPlugin;
import org.terasology.module.behaviors.components.MinionMoveComponent;
import org.terasology.module.behaviors.systems.MinionMoveSystem;
import org.terasology.module.behaviors.systems.PluginSystem;

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
    private MinionMoveSystem flexibleMovementSystem;

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {

        MinionMoveComponent minionMoveComponent = actor.getComponent(MinionMoveComponent.class);
        JPSPlugin pathfindingPlugin = pluginSystem.getMovementPlugin(actor.getEntity()).getJpsPlugin(actor.getEntity());
        if (minionMoveComponent == null || pathfindingPlugin == null) {
            return BehaviorState.FAILURE;
        }
//            for(Vector3i pos : actor().getComponent(MinionMoveComponent.class).getPath()) {
//                if(!pathfindingPlugin.isWalkable(pos)) {
//                    return Status.FAILURE;
//                }
//            }
        return BehaviorState.SUCCESS;
    }
}
