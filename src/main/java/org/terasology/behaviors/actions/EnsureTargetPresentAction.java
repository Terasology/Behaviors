// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.actions;

import org.terasology.behaviors.minion.move.MinionMoveComponent;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;

@BehaviorAction(name = "ensure_target_present")
public class EnsureTargetPresentAction extends BaseAction {
    @Override
    public BehaviorState modify(Actor actor, BehaviorState behaviorState) {
        MinionMoveComponent minionMoveComponent = actor.getComponent(MinionMoveComponent.class);

        if (minionMoveComponent.target != null) {
            return BehaviorState.SUCCESS;
        }
        return BehaviorState.FAILURE;
    }
}
