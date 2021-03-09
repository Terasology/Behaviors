// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.grid;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.network.ClientComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.NUIManager;
import org.terasology.input.ButtonState;

/**
 *
 */
@RegisterSystem
public class GridRenderSystem extends BaseComponentSystem {
    @In
    private NUIManager nuiManager;

    @ReceiveEvent(components = ClientComponent.class)
    public void onToggleConsole(GridRendererButton event, EntityRef entity) {
        if (event.getState() == ButtonState.DOWN) {
            nuiManager.toggleScreen("pathfinding:gridrenderer");
            event.consume();
        }
    }
}
