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

    /**
     * Make an entity with the a {@link FleeOnHitComponent} flee from the instigator when being damaged.
     *
     * @param event
     * @param entity the entity being damaged
     * @param fleeOnHitComponent only entities with this component flee if they take damage
     * @param characterMovementComponent a fleeing entity needs a character movement component to allow it to actually flee from the
     *         attacker
     */
    @ReceiveEvent
    public void onDamage(OnDamagedEvent event, EntityRef entity,
                         FleeOnHitComponent fleeOnHitComponent,
                         CharacterMovementComponent characterMovementComponent) {

        // Make entity flee (update existing FleeingComponent or add fresh component if not present)
        entity.upsertComponent(FleeingComponent.class, maybeFleeingComponent -> {
            FleeingComponent fleeingComponent = maybeFleeingComponent.orElse(new FleeingComponent());
            fleeingComponent.instigator = event.getInstigator();
            fleeingComponent.minDistance = fleeOnHitComponent.minDistance;
            return fleeingComponent;
        });

        // Increase speed by multiplier factor
        characterMovementComponent.speedMultiplier = fleeOnHitComponent.speedMultiplier;
        entity.saveComponent(characterMovementComponent);
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
