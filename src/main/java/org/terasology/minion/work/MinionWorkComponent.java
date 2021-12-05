// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion.work;

import org.joml.Vector3i;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Work's minion component. Indicates, the minion is currently executing a work.
 *
 */

public class MinionWorkComponent implements Component<MinionWorkComponent> {
    public transient EntityRef currentWork;
    public final transient Vector3i target = new Vector3i();
    public transient float cooldown;
    public transient Work filter;
    public transient boolean workSearchDone;

    public MinionWorkComponent() {
    }

    @Override
    public void copyFrom(MinionWorkComponent other) {
        this.currentWork = other.currentWork;
        this.target.set(other.target);
        this.cooldown = other.cooldown;
        this.filter = other.filter;
        this.workSearchDone = other.workSearchDone;
    }
}
