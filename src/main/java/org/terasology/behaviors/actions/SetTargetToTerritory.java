// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.actions;

import org.terasology.behaviors.components.TerritoryDistance;
import org.terasology.behaviors.minion.move.MinionMoveComponent;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.math.geom.Vector3f;

@BehaviorAction(name = "set_target_territory")
public class SetTargetToTerritory extends BaseAction {
    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        if (!actor.hasComponent(TerritoryDistance.class) || !actor.hasComponent(MinionMoveComponent.class)) {
            return BehaviorState.FAILURE;
        }

        Vector3f territory = actor.getComponent(TerritoryDistance.class).location;

        MinionMoveComponent moveComponent = actor.getComponent(MinionMoveComponent.class);
        if (moveComponent.target.equals(territory)) {
            return BehaviorState.SUCCESS;
        }

        moveComponent.target = territory;
        actor.save(moveComponent);

        return BehaviorState.SUCCESS;
    }
}
