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
