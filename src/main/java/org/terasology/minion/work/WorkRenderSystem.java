/*
 * Copyright 2018 MovingBlocks
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
package org.terasology.minion.work;

import org.joml.Vector3f;
import org.joml.Vector3i;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.entitySystem.systems.RenderSystem;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.world.selection.BlockSelectionRenderer;
import org.terasology.engine.utilities.Assets;
import org.terasology.engine.world.block.BlockComponent;

@RegisterSystem(RegisterMode.CLIENT)
public class WorkRenderSystem extends BaseComponentSystem implements RenderSystem {
    @In
    private EntityManager entityManager;
    private BlockSelectionRenderer selectionRenderer;

    @Override
    public void initialise() {
        selectionRenderer = new BlockSelectionRenderer(Assets.getTexture("engine:selection").orElse(null));
    }

    @Override
    public void renderOverlay() {
        selectionRenderer.beginRenderOverlay();
        Vector3i pos = new Vector3i();
        for (EntityRef entityRef : entityManager.getEntitiesWith(BlockComponent.class, WorkTargetComponent.class)) {
            LocationComponent location = entityRef.getComponent(LocationComponent.class);
            Vector3f worldPosition = location.getWorldPosition(new Vector3f());
            pos.set((int) worldPosition.x, (int) worldPosition.y, (int) worldPosition.z);
            WorkTargetComponent work = entityRef.getComponent(WorkTargetComponent.class);
            if (work.isRequestable(entityRef)) {
                selectionRenderer.renderMark(pos);
            } else if (work.isAssignable(entityRef)) {
                selectionRenderer.renderMark2(pos);
            }
        }
        selectionRenderer.endRenderOverlay();
    }

    @Override
    public void renderAlphaBlend() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void renderOpaque() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void renderShadows() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void shutdown() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
