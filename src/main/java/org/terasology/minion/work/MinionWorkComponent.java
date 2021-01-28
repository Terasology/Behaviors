// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion.work;

import org.joml.Vector3i;
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;

/**
 * Work's minion component. Indicates, the minion is currently executing a work.
 *
 */

public class MinionWorkComponent implements Component {
    public transient EntityRef currentWork;
    public transient Vector3i target;
    public transient float cooldown;
    public transient Work filter;
    public transient boolean workSearchDone;

    public MinionWorkComponent() {
    }
}
