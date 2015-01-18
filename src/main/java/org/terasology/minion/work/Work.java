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
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.navgraph.WalkableBlock;

import java.util.List;

/**
 * Defines a work on a block.
 *
 * @author synopia
 */
public interface Work {
    /**
     * Returns list of positions that are valid to work on this work.
     */
    List<WalkableBlock> getTargetPositions(EntityRef block);

    boolean canMinionWork(EntityRef block, EntityRef minion);

    boolean isAssignable(EntityRef block);

    void letMinionWork(EntityRef block, EntityRef minion);

    boolean isRequestable(EntityRef block);

    SimpleUri getUri();

    float cooldownTime();
}
