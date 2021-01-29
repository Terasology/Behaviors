// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.actions;

import org.terasology.behaviors.system.NightTrackerSystem;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.In;

/**
 * Behavior node that checks the current night status, succeeds at nighttime and fails at daytime.
 */
@BehaviorAction(name = "check_nighttime")
public class CheckNightAction extends BaseAction {

    @In
    private NightTrackerSystem nightTrackerSystem;

    @Override
    public void construct(Actor actor) {
        // TODO: Temporary fix for injection malfunction in actions, ideally remove this in the future.
        nightTrackerSystem = CoreRegistry.get(NightTrackerSystem.class);
    }

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        if (nightTrackerSystem.isNight()) {
            return BehaviorState.SUCCESS;
        }
        return BehaviorState.FAILURE;
    }

}
