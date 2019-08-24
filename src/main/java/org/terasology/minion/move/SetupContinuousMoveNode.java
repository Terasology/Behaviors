/*
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.minion.move;

import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
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
            moveComponent.target = block.getBlockPosition().toVector3f();
            actor.save(moveComponent);

            return BehaviorState.SUCCESS;
        }

        return BehaviorState.FAILURE;
    }

}
