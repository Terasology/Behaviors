// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.behaviors.system;

import org.terasology.behaviors.components.FindNearbyPlayersComponent;
import org.terasology.behaviors.components.FollowComponent;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.console.commandSystem.annotations.Command;
import org.terasology.engine.logic.console.commandSystem.annotations.Sender;
import org.terasology.engine.network.ClientComponent;
import org.terasology.engine.registry.In;
import org.terasology.minion.move.MinionMoveComponent;

@RegisterSystem
public class BehaviorCommands extends BaseComponentSystem {

    @In
    private EntityManager entityManager;

    @Command(value = "behaviors:follow", shortDescription = "Let's all NPCs follow you.")
    public void follow(
            @Sender EntityRef client
    ) {
        // retrieve player entity associated with sending client
        EntityRef player = client.getComponent(ClientComponent.class).character;

        // get all entities with MinionMove component
        Iterable<EntityRef> movingNPCEntities = entityManager.getEntitiesWith(MinionMoveComponent.class);
        for (EntityRef entity : movingNPCEntities) {
            // set FollowComponent for all to entity of command sender
            entity.upsertComponent(FollowComponent.class, followComponent -> {
                FollowComponent component = followComponent.orElse(new FollowComponent());
                component.entityToFollow = player;
                return component;
            });
            entity.upsertComponent(FindNearbyPlayersComponent.class, findNearbyPlayersComponent -> {
                FindNearbyPlayersComponent component = findNearbyPlayersComponent.orElse(new FindNearbyPlayersComponent());
                component.searchRadius = 50f;
                return component;
            });
        }
    }

    @Command(value = "behaviors:stay", shortDescription = "Let's all NPCs stay where they are.")
    public void stay(
            @Sender EntityRef client
    ) {
        // retrieve player entity associated with sending client
        EntityRef player = client.getComponent(ClientComponent.class).character;

        // get all entities with MinionMove component
        Iterable<EntityRef> movingNPCEntities = entityManager.getEntitiesWith(MinionMoveComponent.class);
        for (EntityRef entity : movingNPCEntities) {
            // set FollowComponent for all to null entity
            entity.upsertComponent(FollowComponent.class, followComponent -> {
                FollowComponent component = followComponent.orElse(new FollowComponent());
                component.entityToFollow = EntityRef.NULL;
                return component;
            });
            entity.removeComponent(FindNearbyPlayersComponent.class);
        }
    }

}
