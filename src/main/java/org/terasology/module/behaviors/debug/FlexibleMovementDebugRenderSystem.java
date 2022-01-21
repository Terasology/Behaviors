// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.debug;

import org.joml.Vector3ic;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.entitySystem.systems.RenderSystem;
import org.terasology.engine.registry.In;
import org.terasology.engine.registry.Share;
import org.terasology.engine.rendering.world.selection.BlockSelectionRenderer;
import org.terasology.engine.utilities.Assets;
import org.terasology.module.behaviors.components.MinionMoveComponent;

@RegisterSystem(RegisterMode.CLIENT)
@Share(FlexibleMovementDebugRenderSystem.class)
public class FlexibleMovementDebugRenderSystem extends BaseComponentSystem implements RenderSystem {
    private BlockSelectionRenderer selectionRenderer;

    @In
    private EntityManager entityManager;

    @Override
    public void initialise() {
        selectionRenderer = new BlockSelectionRenderer(Assets.getTexture("engine:selection").get());
    }

    @Override
    public void renderOverlay() {
        selectionRenderer.beginRenderOverlay();
        for (EntityRef entity : entityManager.getEntitiesWith(MinionMoveComponent.class)) {
            MinionMoveComponent minionMoveComponent = entity.getComponent(MinionMoveComponent.class);
            for (Vector3ic pos : minionMoveComponent.getPath()) {
                selectionRenderer.renderMark2(pos);
            }
        }
        selectionRenderer.endRenderOverlay();
    }
}
