// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.actions;

import org.joml.Vector3i;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.module.behaviors.components.MinionMoveComponent;

import java.util.Collections;

/**
 * Constructs a dummy two-step path consisting of only the actor's current position and goal position Meant as a cheap fallback when full
 * pathing is not needed or possible
 * <p/>
 * SUCCESS: Always
 */
@BehaviorAction(name = "find_dummy_path_to")
public class FindDummyPathTo extends BaseAction {

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        MinionMoveComponent movement = actor.getComponent(MinionMoveComponent.class);

        Vector3i goal = movement.getPathGoal();

        if (goal == null) {
            return BehaviorState.FAILURE;
        }

        movement.setPath(Collections.singletonList(goal));
        actor.save(movement);

        return BehaviorState.SUCCESS;
    }
}
