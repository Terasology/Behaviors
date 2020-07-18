/*
 * Copyright 2020 MovingBlocks
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
package org.terasology.behaviors.actions;

import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.behaviors.system.NightTrackerSystem;
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
