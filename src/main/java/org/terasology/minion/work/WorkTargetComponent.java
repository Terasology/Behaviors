/*
 * Copyright 2014 MovingBlocks
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
package org.terasology.minion.work;

import org.terasology.engine.SimpleUri;
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.navgraph.WalkableBlock;
import org.terasology.registry.CoreRegistry;
import org.terasology.world.block.ForceBlockActive;

import java.util.List;

/**
 * Work's block component. Using this component, jobs can be assigned to individual blocks
 *
 * @author synopia
 */
@ForceBlockActive
public class WorkTargetComponent implements Component, Work {
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
    public List<WalkableBlock> getTargetPositions(EntityRef block) {
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
}
