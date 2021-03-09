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
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.registry.In;
import org.terasology.navgraph.WalkableBlock;
import org.terasology.pathfinding.componentSystem.PathfinderSystem;

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
            moveComponent.target = new Vector3f(block.getBlockPosition());
            actor.save(moveComponent);
        }
        return BehaviorState.SUCCESS;
    }

}
