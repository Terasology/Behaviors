// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.actions;

import org.joml.Vector3f;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.world.block.Blocks;
import org.terasology.module.behaviors.components.MinionMoveComponent;
import org.terasology.module.behaviors.components.TerritoryComponent;


@BehaviorAction(name = "set_target_territory")
public class SetTargetToTerritory extends BaseAction {
    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        if (!actor.hasComponent(TerritoryComponent.class) || !actor.hasComponent(MinionMoveComponent.class)) {
            return BehaviorState.FAILURE;
        }

        Vector3f territory = actor.getComponent(TerritoryComponent.class).location;

        MinionMoveComponent moveComponent = actor.getComponent(MinionMoveComponent.class);
        if (moveComponent.target.equals(territory)) {
            return BehaviorState.SUCCESS;
        }

        moveComponent.target = Blocks.toBlockPos(territory);
        actor.save(moveComponent);

        return BehaviorState.SUCCESS;
    }
}
