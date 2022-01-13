// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.systems;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.Share;
import org.terasology.engine.world.sun.OnDawnEvent;
import org.terasology.engine.world.sun.OnDuskEvent;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

/**
 * Tracks the current night status for time-based behavior trees.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
@Share(value = NightTrackerSystem.class)
public class NightTrackerSystem extends BaseComponentSystem {

    // TODO: The assumption is made that this system is started with daylight, replace with a proper check on start.
    private boolean isSunUp = true;

    @ReceiveEvent
    public void onDawnEvent(OnDawnEvent event, EntityRef entityRef) {
        isSunUp = true;
    }

    @ReceiveEvent
    public void onDuskEvent(OnDuskEvent event, EntityRef entityRef) {
        isSunUp = false;
    }

    /**
     * Returns current night status, true if the sun is down, false otherwise.
     *
     * @return The current night status of the world.
     */
    public boolean isNight() {
        return !isSunUp;
    }

}
