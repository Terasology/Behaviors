// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.grid;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.input.ButtonState;
import org.terasology.network.ClientComponent;
import org.terasology.registry.In;
import org.terasology.rendering.nui.NUIManager;

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
