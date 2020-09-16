// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.minion.move;

import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.math.geom.Vector3f;
import org.terasology.pathfinding.navgraph.WalkableBlock;

/**
 * Call child node, as long as the actor has not reached the end of the path. Sets <b>MinionMoveComponent.target</b> to
 * next step in path.<br/> Old construct() from original {@link MoveAlongPathNode} moved to {@link
 * SetupContinuousMoveNode} to allow for path travelling to be interrupted. This enables a character to follow a moving
 * target. <br/>
 * <b>SUCCESS</b>: when actor has reached end of path.<br/>
 * <b>FAILURE</b>: if no path was found previously.<br/>
 * <br/>
 */
@BehaviorAction(name = "move_along_path_continuous", isDecorator = true)
public class ContinuousMoveAlongPathNode extends BaseAction {

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        MinionMoveComponent moveComponent = actor.getComponent(MinionMoveComponent.class);
        if (result != BehaviorState.SUCCESS) {
            return result;
        }
        moveComponent.currentIndex++;
        if (moveComponent.currentIndex < moveComponent.path.size()) {
            WalkableBlock block = moveComponent.path.get(moveComponent.currentIndex);
            Vector3f pos = block.getBlockPosition().toVector3f();
            pos.add(new Vector3f(0, 1, 0));
            moveComponent.target = pos;
            actor.save(moveComponent);
            return BehaviorState.RUNNING;
        } else {
            moveComponent.path = null;
            actor.save(moveComponent);
            return BehaviorState.SUCCESS;
        }
    }

}
