// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion.move;

import org.joml.Vector3f;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.math.JomlUtil;
import org.terasology.navgraph.WalkableBlock;
import org.terasology.pathfinding.componentSystem.PathfinderSystem;
import org.terasology.registry.In;

/**
 * Set <b>MinionMoveComponent.target</b> to the block below local player.<br/>
 * <br/>
 * Always returns <b>SUCCESS</b>.<br/>
 * <br/>
 * Auto generated javadoc - modify README.markdown instead!
 */
@BehaviorAction(name = "set_target_local_player")
public class SetTargetLocalPlayerNode extends BaseAction {
    @In
    private LocalPlayer localPlayer;
    @In
    private PathfinderSystem pathfinderSystem;

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        Vector3f position = localPlayer.getPosition(new Vector3f());
        WalkableBlock block = pathfinderSystem.getBlock(position);
        if (block != null) {
            MinionMoveComponent moveComponent = actor.getComponent(MinionMoveComponent.class);
            moveComponent.target = JomlUtil.from(new Vector3f(block.getBlockPosition()));
            actor.save(moveComponent);
        }
        return BehaviorState.SUCCESS;
    }

}
