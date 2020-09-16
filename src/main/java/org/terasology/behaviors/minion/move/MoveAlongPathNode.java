// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.minion.move;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.math.geom.Vector3f;
import org.terasology.pathfinding.componentSystem.PathRenderSystem;
import org.terasology.pathfinding.model.Path;
import org.terasology.pathfinding.navgraph.WalkableBlock;

/**
 * Call child node, as long as the actor has not reached the end of the path. Sets <b>MinionMoveComponent.target</b> to
 * next step in path.<br/> <br/>
 * <b>SUCCESS</b>: when actor has reached end of path.<br/>
 * <b>FAILURE</b>: if no path was found previously.<br/>
 * <br/> Auto generated javadoc - modify README.markdown instead!
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
            logger.info("Start moving along path to step " + moveComponent.currentIndex + " " + block.getBlockPosition());
            moveComponent.target = block.getBlockPosition().toVector3f();
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
            logger.info(" Continue moving along path to step " + moveComponent.currentIndex + " " + block.getBlockPosition());
            Vector3f pos = block.getBlockPosition().toVector3f();
            pos.add(new Vector3f(0, 1, 0));
            moveComponent.target = pos;
            actor.save(moveComponent);
            return BehaviorState.RUNNING;
        } else {
            pathRenderSystem.removePath(moveComponent.path);
            LocationComponent locationComponent = actor.getComponent(LocationComponent.class);
            logger.info("Finished moving along path pos = " + locationComponent.getWorldPosition() + " block = " + moveComponent.currentBlock);
            return BehaviorState.SUCCESS;
        }
    }

}
