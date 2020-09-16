// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.minion.move;

import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.registry.In;
import org.terasology.math.geom.Vector3f;
import org.terasology.pathfinding.componentSystem.PathfinderSystem;
import org.terasology.pathfinding.navgraph.WalkableBlock;

/**
 * Set <b>MinionMoveComponent.target</b> to the block below local player.<br/> <br/> Always returns <b>SUCCESS</b>.<br/>
 * <br/> Auto generated javadoc - modify README.markdown instead!
 */
@BehaviorAction(name = "set_target_local_player")
public class SetTargetLocalPlayerNode extends BaseAction {
    @In
    private LocalPlayer localPlayer;
    @In
    private PathfinderSystem pathfinderSystem;

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        Vector3f position = localPlayer.getPosition();
        WalkableBlock block = pathfinderSystem.getBlock(position);
        if (block != null) {
            MinionMoveComponent moveComponent = actor.getComponent(MinionMoveComponent.class);
            moveComponent.target = block.getBlockPosition().toVector3f();
            actor.save(moveComponent);
        }
        return BehaviorState.SUCCESS;
    }

}
