// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion.work;

import org.joml.Vector3f;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.math.JomlUtil;
import org.terasology.minion.move.MinionMoveComponent;
import org.terasology.navgraph.WalkableBlock;

import java.util.List;

/**
 * Set <b>MinionMoveComponent</b>'s target to the work's target.<br/>
 * <br/>
 * <b>SUCCESS</b>: if valid work target position found.<br/>
 * <b>FAILURE</b>: otherwise<br/>
 * <br/>
 * Auto generated javadoc - modify README.markdown instead!
 */
@BehaviorAction(name = "set_target_work")
public class SetTargetToWorkNode extends BaseAction {
    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        EntityRef work = actor.getComponent(MinionWorkComponent.class).currentWork;
        if (work != null) {
            WorkTargetComponent workTargetComponent = work.getComponent(WorkTargetComponent.class);
            List<WalkableBlock> targetPositions = workTargetComponent.getWork().getTargetPositions(work);
            if (targetPositions.size() > 0) {
                WalkableBlock block = targetPositions.get(0);
                MinionMoveComponent moveComponent = actor.getComponent(MinionMoveComponent.class);
                moveComponent.target = JomlUtil.from(new Vector3f(block.getBlockPosition()));
                actor.save(moveComponent);
                return BehaviorState.SUCCESS;
            }
        }
        return BehaviorState.FAILURE;
    }
}
