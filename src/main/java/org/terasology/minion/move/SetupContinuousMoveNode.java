// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion.move;

import org.joml.Vector3f;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.math.JomlUtil;
import org.terasology.navgraph.WalkableBlock;
import org.terasology.pathfinding.model.Path;

/**
 * Essential setup for a character to travel along a determined path. This is exported from
 * {@link MoveAlongPathNode#construct(Actor)}, which allows for characters to interrupt travelling along a path.
 * This is essential for characters following a moving object.
 */
@BehaviorAction(name = "setup_continuous_pathfinding")
public class SetupContinuousMoveNode extends BaseAction {

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {

        MinionMoveComponent moveComponent = actor.getComponent(MinionMoveComponent.class);
        if (moveComponent != null && moveComponent.path != null && moveComponent.path != Path.INVALID) {
            moveComponent.currentIndex = 0;
            WalkableBlock block = moveComponent.path.get(moveComponent.currentIndex);
            moveComponent.target = JomlUtil.from(new Vector3f(block.getBlockPosition()));
            actor.save(moveComponent);

            return BehaviorState.SUCCESS;
        }

        return BehaviorState.FAILURE;
    }
}
