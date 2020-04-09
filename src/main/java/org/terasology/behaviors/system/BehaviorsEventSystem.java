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
package org.terasology.behaviors.system;

import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.characters.CharacterMovementComponent;
import org.terasology.logic.health.event.OnDamagedEvent;
import org.terasology.behaviors.components.FollowComponent;
import org.terasology.registry.In;
import org.terasology.behaviors.components.AttackOnHitComponent;
import org.terasology.behaviors.components.FleeingComponent;
import org.terasology.behaviors.components.FleeOnHitComponent;


/*
 * Listens for events relevant to this module's animals and responds as necessary
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class BehaviorsEventSystem extends BaseComponentSystem {

    @In
    private Time time;

    @ReceiveEvent(components = FleeOnHitComponent.class)
    public void onDamage(OnDamagedEvent event, EntityRef entity) {

        // Make entity flee
        FleeingComponent fleeingComponent = new FleeingComponent();
        fleeingComponent.instigator = event.getInstigator();
        fleeingComponent.minDistance = entity.getComponent(FleeOnHitComponent.class).minDistance;
        entity.addOrSaveComponent(fleeingComponent);

        // Increase speed by multiplier factor
        CharacterMovementComponent characterMovementComponent = entity.getComponent(CharacterMovementComponent.class);
        characterMovementComponent.speedMultiplier = entity.getComponent(FleeOnHitComponent.class).speedMultiplier;
        entity.addOrSaveComponent(characterMovementComponent);

    }

    /**
     * Updates the AttackOnHitComponent with information about the hit
     */
    @ReceiveEvent(components = AttackOnHitComponent.class)
    public void onDamage(OnDamagedEvent event, EntityRef entity, AttackOnHitComponent attackOnHitComponent) {
        attackOnHitComponent.instigator = event.getInstigator();
        attackOnHitComponent.timeWhenHit = time.getGameTimeInMs();
        entity.saveComponent(attackOnHitComponent);
        FollowComponent followComponent = new FollowComponent();
        followComponent.entityToFollow = event.getInstigator();
        entity.addOrSaveComponent(followComponent);
    }


}
