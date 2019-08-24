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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.navgraph.WalkableBlock;
import org.terasology.pathfinding.componentSystem.PathRenderSystem;
import org.terasology.pathfinding.model.Path;
import org.terasology.registry.CoreRegistry;

/**
 * Call child node, as long as the actor has not reached the end of the path. Sets <b>MinionMoveComponent.target</b> to
 * next step in path.<br/> Old construct() from original {@link MoveAlongPathNode} moved to
 * {@link SetupContinuousMoveNode} to allow for path travelling to be interrupted. This enables a character to follow
 * a moving target.
 * <br/>
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
