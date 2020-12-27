/*
 * Copyright 2015 MovingBlocks
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

import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.logic.location.LocationComponent;
import org.terasology.navgraph.WalkableBlock;
import org.terasology.pathfinding.componentSystem.PathRenderSystem;
import org.terasology.pathfinding.model.Path;
import org.terasology.registry.CoreRegistry;

/**
 * Call child node, as long as the actor has not reached the end of the path. Sets <b>MinionMoveComponent.target</b> to next step in path.<br/>
 * <br/>
 * <b>SUCCESS</b>: when actor has reached end of path.<br/>
 * <b>FAILURE</b>: if no path was found previously.<br/>
 * <br/>
 * Auto generated javadoc - modify README.markdown instead!
 */
@BehaviorAction(name = "move_along_path", isDecorator = true)
public class MoveAlongPathNode extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(MoveAlongPathNode.class);

    // @In
    private transient PathRenderSystem pathRenderSystem;

    @Override
    public void construct(Actor actor) {
        // TODO: Temporary fix for injection malfunction in actions, ideally remove this in the future.
        pathRenderSystem = CoreRegistry.get(PathRenderSystem.class);

        MinionMoveComponent moveComponent = actor.getComponent(MinionMoveComponent.class);
        if (moveComponent != null && moveComponent.path != null && moveComponent.path != Path.INVALID) {
            pathRenderSystem.addPath(moveComponent.path);
            moveComponent.currentIndex = 0;
            WalkableBlock block = moveComponent.path.get(moveComponent.currentIndex);
            logger.debug("Start moving along path to step " + moveComponent.currentIndex + " " + block.getBlockPosition());
            moveComponent.target = new Vector3f(block.getBlockPosition());
            actor.save(moveComponent);
        }
    }

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        MinionMoveComponent moveComponent = actor.getComponent(MinionMoveComponent.class);
        if (result != BehaviorState.SUCCESS) {
            return result;
        }
        moveComponent.currentIndex++;
        if (moveComponent.currentIndex < moveComponent.path.size()) {
            WalkableBlock block = moveComponent.path.get(moveComponent.currentIndex);
            logger.debug(" Continue moving along path to step " + moveComponent.currentIndex + " " + block.getBlockPosition());
            Vector3f pos = new Vector3f(block.getBlockPosition());
            pos.add(new Vector3f(0, 1, 0));
            moveComponent.target = pos;
            actor.save(moveComponent);
            return BehaviorState.RUNNING;
        } else {
            pathRenderSystem.removePath(moveComponent.path);
            LocationComponent locationComponent = actor.getComponent(LocationComponent.class);
            logger.debug("Finished moving along path pos = " + locationComponent.getWorldPosition(new Vector3f()) + " " +
                "block = " + moveComponent.currentBlock);
            return BehaviorState.SUCCESS;
        }
    }

}
