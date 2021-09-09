// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.system;

import org.terasology.behaviors.components.AttackOnHitComponent;
import org.terasology.behaviors.components.FleeOnHitComponent;
import org.terasology.behaviors.components.FleeingComponent;
import org.terasology.behaviors.components.FollowComponent;
import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.characters.CharacterMovementComponent;
import org.terasology.engine.registry.In;
import org.terasology.module.health.events.OnDamagedEvent;


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
