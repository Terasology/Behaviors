// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.minion.move;

import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.pathfinding.model.Path;
import org.terasology.pathfinding.navgraph.WalkableBlock;

/**
 * Essential setup for a character to travel along a determined path. This is exported from {@link
 * MoveAlongPathNode#construct(Actor)}, which allows for characters to interrupt travelling along a path. This is
 * essential for characters following a moving object.
 */
@BehaviorAction(name = "setup_continuous_pathfinding")
public class SetupContinuousMoveNode extends BaseAction {

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {

        MinionMoveComponent moveComponent = actor.getComponent(MinionMoveComponent.class);
        if (moveComponent != null && moveComponent.path != null && moveComponent.path != Path.INVALID) {
            moveComponent.currentIndex = 0;
            WalkableBlock block = moveComponent.path.get(moveComponent.currentIndex);
            moveComponent.target = block.getBlockPosition().toVector3f();
            actor.save(moveComponent);

            return BehaviorState.SUCCESS;
        }

        return BehaviorState.FAILURE;
    }

}
