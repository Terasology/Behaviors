/*
 * Copyright 2017 MovingBlocks
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
package org.terasology.behaviors.components;

import org.terasology.entitySystem.Component;

/**
 * If this components is attached to an NPC entity it will exhibit the flee-on-hit behavior
 * When hit, the NPC will run with a speed of `speedMultiplier`*normalSpeed
 * till it is at a safe `minDistance` from the damage inflicter- `instigator`.
 * When it reaches a safe distance the instigator is set to null.
 */
public class FleeOnHitComponent implements Component {
    /* Minimum distance from instigator after which the NPC will stop 'flee'ing */
    public float minDistance = 10f;
    /* Speed factor by which flee speed increases */
    public float speedMultiplier = 1.2f;
}