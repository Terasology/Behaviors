// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion.move;

import org.joml.Vector3f;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.navgraph.WalkableBlock;

/**
 * Call child node, as long as the actor has not reached the end of the path. 
 * <br/>
 * Sets {@link MinionMoveComponent#target} to next step in path.
 * <br/> 
 * Old {@code construct()} from original {@link MoveAlongPathNode} moved to {@link SetupContinuousMoveNode} to allow for path travelling to be interrupted.
 * This enables a character to follow a moving target.
 * <br/>
 * <b>SUCCESS</b>: when actor has reached end of path.<br/>
 * <b>FAILURE</b>: if no path was found previously.<br/>
 * <br/>
 */
@BehaviorAction(name = "move_along_path_continuous", isDecorator = true)
public class ContinuousMoveAlongPathNode extends BaseAction {

    //TODO: What's the difference between ContinuousMoveAlongPathNode and MoveAlongPathNode and do we need both?
    //      The comments indicate that 
    //          (SetupContinuousMoveNode + ContinuousMoveAlongPathNode) == MoveAlongPathNode + interrupt

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        MinionMoveComponent moveComponent = actor.getComponent(MinionMoveComponent.class);
        if (result != BehaviorState.SUCCESS) {
            return result;
        }
        moveComponent.currentIndex++;
        if (moveComponent.currentIndex < moveComponent.path.size()) {
            WalkableBlock block = moveComponent.path.get(moveComponent.currentIndex);
            moveComponent.target = new Vector3f(block.getBlockPosition()).add(new Vector3f(0, 1, 0));
            actor.save(moveComponent);
            return BehaviorState.RUNNING;
        } else {
            moveComponent.path = null;
            actor.save(moveComponent);
            return BehaviorState.SUCCESS;
        }
    }
}
