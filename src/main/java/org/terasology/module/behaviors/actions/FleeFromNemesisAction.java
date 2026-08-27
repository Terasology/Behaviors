// Copyright 2023 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.module.behaviors.actions;

import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.module.behaviors.components.FindNearbyNemesisComponent;
import org.terasology.module.behaviors.components.FleeingComponent;

@BehaviorAction(name = "flee_from_nemesis")

/**
 * This Action is used for the combinedCritter behaviour
 * It's used for finding an Entity of a configurable Nemesis-Group and fleeing from them.
 */

public class FleeFromNemesisAction extends BaseAction {
    @Override
    public void construct(Actor actor) {
        FleeingComponent fleeingComponent = new FleeingComponent();
        FindNearbyNemesisComponent component = actor.getComponent(FindNearbyNemesisComponent.class);
        fleeingComponent.instigator = component.closestNemesis;
        actor.save(fleeingComponent);

    }

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        return BehaviorState.SUCCESS;
    }
}
