// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.debug;

import org.joml.Vector3ic;
import org.terasology.engine.config.Config;
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
@Share(DebugRenderSystem.class)
public class DebugRenderSystem extends BaseComponentSystem implements RenderSystem {
    private BlockSelectionRenderer selectionRenderer;

    @In
    private Config config;

    @In
    private EntityManager entityManager;

    @Override
    public void initialise() {
        selectionRenderer = new BlockSelectionRenderer(Assets.getTexture("engine:selection").get());
    }

    @Override
    public void renderOverlay() {
        //TODO: Extend the RenderingDebugConfig with a specific flag for rendering the movement path.
        //      The RenderingDebugConfig holds more specific flags which debug modes are enabled, but that config is not extensible.
        //      How would we register new debug modes like this one for pathfinding? Should this be tied to debug mode in general, or
        //      should it be possible to toggle the debug rendering via console command, similar to RenderingDebugCommands?
        if (config.getRendering().getDebug().isEnabled()) {
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
}
