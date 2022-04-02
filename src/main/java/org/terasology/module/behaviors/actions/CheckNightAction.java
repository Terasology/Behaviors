// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.actions;

import org.terasology.module.behaviors.systems.NightTrackerSystem;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.registry.In;

/**
 * Behavior node that checks the current night status, succeeds at nighttime and fails at daytime.
 */
@BehaviorAction(name = "check_nighttime")
public class CheckNightAction extends BaseAction {

    @In
    private NightTrackerSystem nightTrackerSystem;

    @Override
    public void construct(Actor actor) {
        // TODO: Temporary fix for injection malfunction, remove once https://github.com/MovingBlocks/Terasology/issues/5004 is fixed.
        if (nightTrackerSystem == null) {
            nightTrackerSystem = CoreRegistry.get(NightTrackerSystem.class);
        }
    }

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        if (nightTrackerSystem.isNight()) {
            return BehaviorState.SUCCESS;
        }
        return BehaviorState.FAILURE;
    }

}
