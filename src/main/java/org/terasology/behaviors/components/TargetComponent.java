/*
 * Copyright 2019 MovingBlocks
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
import org.terasology.entitySystem.entity.EntityRef;

/**
 * A component used for when you want an entity to "target" another entity. The reason you may want to target an entity
 * can vary: a curious critter may turn to face a player who moves in close, and continue to track that player
 * if they stay close; a guard may turn towards and start to shoot a player who gets to close enough; a fixed place
 * weapon may also target a player in range.
 */
public class TargetComponent implements Component {
    public EntityRef target = EntityRef.NULL;
}
