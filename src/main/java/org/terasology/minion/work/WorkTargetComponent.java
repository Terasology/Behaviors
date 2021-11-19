// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion.work;

import org.joml.Vector3ic;
import org.terasology.engine.core.SimpleUri;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.world.block.ForceBlockActive;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.List;

/**
 * Work's block component. Using this component, jobs can be assigned to individual blocks
 */
@ForceBlockActive
public class WorkTargetComponent implements Component<WorkTargetComponent>, Work {
    public String workUri;
    public transient EntityRef assignedMinion;
    private transient Work work;

    public WorkTargetComponent() {
    }

    public void setWork(Work work) {
        this.work = work;
        this.workUri = work.getUri().toString();
    }

    public Work getWork() {
        if (work == null) {
            work = CoreRegistry.get(WorkFactory.class).getWork(workUri);
        }
        return work;
    }

    @Override
    public List<Vector3ic> getTargetPositions(EntityRef block) {
        return getWork().getTargetPositions(block);
    }

    @Override
    public boolean canMinionWork(EntityRef block, EntityRef minion) {
        return getWork().canMinionWork(block, minion);
    }

    @Override
    public boolean isAssignable(EntityRef block) {
        return getWork().isAssignable(block);
    }

    @Override
    public void letMinionWork(EntityRef block, EntityRef minion) {
        getWork().letMinionWork(block, minion);
    }

    @Override
    public float cooldownTime() {
        return getWork().cooldownTime();
    }

    @Override
    public boolean isRequestable(EntityRef block) {
        return getWork().isRequestable(block);
    }

    @Override
    public SimpleUri getUri() {
        return getWork().getUri();
    }

    @Override
    public void copyFrom(WorkTargetComponent other) {
        this.workUri = other.workUri;
        this.assignedMinion = other.assignedMinion;
        this.work = other.work;
    }
}
