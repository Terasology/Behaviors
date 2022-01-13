// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.debug;

import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.behavior.BehaviorComponent;
import org.terasology.engine.logic.behavior.asset.BehaviorTree;
import org.terasology.engine.logic.common.ActivateEvent;
import org.terasology.engine.registry.In;
import org.terasology.engine.registry.Share;
import org.terasology.engine.world.block.Blocks;
import org.terasology.gestalt.assets.management.AssetManager;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.module.behaviors.components.MinionMoveComponent;


@Share(DebugMoveToSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class DebugMoveToSystem extends BaseComponentSystem {
    @In private EntityManager entityManager;
    @In private AssetManager assetManager;

    @ReceiveEvent(components = DebugMoveToComponent.class)
    public void onDebugMoveToActivated(ActivateEvent event, EntityRef item) {
        for (EntityRef entity : entityManager.getEntitiesWith(MinionMoveComponent.class, BehaviorComponent.class)) {
            MinionMoveComponent component = entity.getComponent(MinionMoveComponent.class);
            BehaviorComponent behaviorComponent = entity.getComponent(BehaviorComponent.class);
            behaviorComponent.tree = assetManager.getAsset("FlexibleMovement:reliableMoveTo", BehaviorTree.class).get();

            component.setPathGoal(Blocks.toBlockPos(event.getHitPosition()));
            component.resetPath();
            entity.saveComponent(component);
            entity.saveComponent(behaviorComponent);
        }
    }
}
