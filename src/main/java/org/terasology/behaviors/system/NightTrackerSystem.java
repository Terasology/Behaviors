// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.system;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.registry.Share;
import org.terasology.world.sun.OnDawnEvent;
import org.terasology.world.sun.OnDuskEvent;

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
