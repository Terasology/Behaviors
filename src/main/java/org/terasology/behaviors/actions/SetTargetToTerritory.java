// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.actions;

import org.joml.Vector3f;
import org.terasology.behaviors.components.TerritoryDistance;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.world.block.Blocks;
import org.terasology.behaviors.components.FlexibleMovementComponent;


@BehaviorAction(name = "flex_set_target_territory")
public class SetTargetToTerritory extends BaseAction {
    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        if (!actor.hasComponent(TerritoryDistance.class) || !actor.hasComponent(FlexibleMovementComponent.class)) {
            return BehaviorState.FAILURE;
        }

        Vector3f territory = actor.getComponent(TerritoryDistance.class).location;

        FlexibleMovementComponent moveComponent = actor.getComponent(FlexibleMovementComponent.class);
        if (moveComponent.target.equals(territory)) {
            return BehaviorState.SUCCESS;
        }

        moveComponent.target = Blocks.toBlockPos(territory);
        actor.save(moveComponent);

        return BehaviorState.SUCCESS;
    }
}
