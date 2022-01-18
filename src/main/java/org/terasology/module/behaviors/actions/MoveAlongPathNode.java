// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.actions;

import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.module.behaviors.components.MinionMoveComponent;

/**
 * Performs a child node along the {@link MinionMoveComponent#getPath()}
 * <p>
 * 1. Sets the {@link MinionMoveComponent#target}
 * <p>
 * 2. Runs the child node until SUCCESS/FAILURE
 * <p>
 * 3. On child SUCCESS, sets target to next waypoint and starts child again
 * <p>
 * 4. On child FAILURE, returns FAILURE
 * <p>
 * 5. When end of path is reached, returns SUCCESS
 */
@BehaviorAction(name = "move_along_path", isDecorator = true)
public class MoveAlongPathNode extends BaseAction {

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        MinionMoveComponent movement = actor.getComponent(MinionMoveComponent.class);
        if (result == BehaviorState.SUCCESS) {
            movement.advancePath();
            if (movement.isPathFinished()) {
                movement.resetPath();
                actor.save(movement);
                return BehaviorState.SUCCESS;
            } else {
                actor.save(movement);
                return BehaviorState.RUNNING;
            }
        }

        return result;
    }
}
