// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion.move;

import com.google.common.collect.Sets;
import org.joml.Vector3f;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.BeforeDeactivateComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.characters.events.HorizontalCollisionEvent;
import org.terasology.logic.characters.events.OnEnterBlockEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.JomlUtil;
import org.terasology.navgraph.NavGraphChanged;
import org.terasology.navgraph.WalkableBlock;
import org.terasology.pathfinding.componentSystem.PathfinderSystem;
import org.terasology.registry.In;

import java.util.Set;

/**
 *
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class MinionMoveSystem extends BaseComponentSystem {
    @In
    private PathfinderSystem pathfinderSystem;

    private Set<EntityRef> entities = Sets.newHashSet();

    @ReceiveEvent
    public void worldChanged(NavGraphChanged event, EntityRef entityRef) {
        for (EntityRef entity : entities) {
            setupEntity(entity);
        }
    }

    @ReceiveEvent
    public void onCollision(HorizontalCollisionEvent event, EntityRef minion, MinionMoveComponent movementComponent) {
        movementComponent.horizontalCollision = true;
        minion.saveComponent(movementComponent);
    }

    @ReceiveEvent
    public void onMinionEntersBlock(OnEnterBlockEvent event, EntityRef minion, LocationComponent locationComponent,
                                    MinionMoveComponent moveComponent) {
        setupEntity(minion);
    }

    @ReceiveEvent
    public void onMinionActivation(OnActivatedComponent event, EntityRef minion, LocationComponent locationComponent,
                                   MinionMoveComponent moveComponent) {
        setupEntity(minion);
        entities.add(minion);
    }

    @ReceiveEvent
    public void onMinionDeactivation(BeforeDeactivateComponent event, EntityRef minion,
                                     LocationComponent locationComponent, MinionMoveComponent moveComponent) {
        entities.remove(minion);
    }

    private void setupEntity(EntityRef minion) {
        MinionMoveComponent moveComponent = minion.getComponent(MinionMoveComponent.class);
        WalkableBlock block = pathfinderSystem.getBlock(minion);
        moveComponent.currentBlock = block;
        if (block != null && moveComponent.target == null) {
            moveComponent.target = JomlUtil.from(new Vector3f(block.getBlockPosition()));
        }
        minion.saveComponent(moveComponent);
    }

    @Override
    public void initialise() {
    }

    @Override
    public void shutdown() {

    }
}
