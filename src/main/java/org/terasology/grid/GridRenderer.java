// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.grid;

import org.joml.Rectanglei;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.grid.renderers.DefaultBlockRenderer;
import org.terasology.grid.renderers.WalkableBlockRenderer;
import org.terasology.grid.renderers.WorkRenderer;
import org.terasology.input.Keyboard;
import org.terasology.logic.characters.CharacterComponent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.logic.selection.ApplyBlockSelectionEvent;
import org.terasology.math.JomlUtil;
import org.terasology.math.Region3i;
import org.terasology.math.geom.Vector3f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3i;
import org.terasology.minion.work.Work;
import org.terasology.minion.work.WorkComponent;
import org.terasology.minion.work.WorkFactory;
import org.terasology.registry.CoreRegistry;
import org.terasology.nui.BaseInteractionListener;
import org.terasology.nui.Canvas;
import org.terasology.nui.Color;
import org.terasology.nui.InteractionListener;
import org.terasology.nui.events.NUIMouseClickEvent;
import org.terasology.nui.events.NUIMouseDragEvent;
import org.terasology.nui.events.NUIMouseReleaseEvent;
import org.terasology.nui.events.NUIMouseWheelEvent;
import org.terasology.nui.layouts.ZoomableLayout;

public class GridRenderer extends ZoomableLayout {
    private static final Logger logger = LoggerFactory.getLogger(GridRenderer.class);

    private DefaultBlockRenderer blockRenderer;
    private WalkableBlockRenderer walkableBlockRenderer;
    private WorkRenderer workRenderer;
    private EntityRenderer entityRenderer;
    private int y;
    private int yDiff = -1;
    private Vector2i startDrag;
    private Vector2i endDrag;
    private InteractionListener listener = new BaseInteractionListener() {
        @Override
        public boolean onMouseClick(NUIMouseClickEvent event) {
            startDrag = event.getRelativeMousePosition();
            endDrag = event.getRelativeMousePosition();
            return true;
        }

        @Override
        public void onMouseDrag(NUIMouseDragEvent event) {
            endDrag = event.getRelativeMousePosition();
        }

        @Override
        public void onMouseRelease(NUIMouseReleaseEvent event) {
            Work work = CoreRegistry.get(WorkFactory.class).getWork("pathfinding:walkToBlock");
            WorkComponent workComponent = new WorkComponent();
            workComponent.uri = work.getUri();
            EntityRef entityRef = CoreRegistry.get(EntityManager.class).create(workComponent, new LocationComponent()
                , new CharacterComponent());

            Vector2f start = screenToWorld(startDrag);
            Vector2f end = screenToWorld(endDrag);
            Vector3i startInt = new Vector3i((int) start.x, y, (int) start.y);
            Vector3i endInt = new Vector3i((int) end.x, y, (int) end.y);
            Region3i rect = Region3i.createFromMinMax(JomlUtil.from(startInt), JomlUtil.from(endInt));
            ApplyBlockSelectionEvent selectionEvent = new ApplyBlockSelectionEvent(entityRef, rect);
            entityRef.send(selectionEvent);
            startDrag = null;
            endDrag = null;
        }

        @Override
        public boolean onMouseWheel(NUIMouseWheelEvent event) {

            if (!event.getKeyboard().isKeyDown(Keyboard.Key.LEFT_SHIFT.getId())) {
                yDiff += event.getWheelTurns() > 0 ? -1 : +1;
            }
            return false;
        }
    };

    public GridRenderer() {
        initialize();
    }

    public GridRenderer(String id) {
        super(id);
        initialize();
    }

    private void initialize() {
        blockRenderer = CoreRegistry.get(DefaultBlockRenderer.class);
        walkableBlockRenderer = CoreRegistry.get(WalkableBlockRenderer.class);
        entityRenderer = CoreRegistry.get(EntityRenderer.class);
        workRenderer = CoreRegistry.get(WorkRenderer.class);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.addInteractionRegion(listener);

        Vector3f playerPosition =
            CoreRegistry.get(LocalPlayer.class).getCharacterEntity().getComponent(LocationComponent.class).getWorldPosition();
        Vector2f windowSize = getWindowSize();
        Vector2f topLeft = new Vector2f(playerPosition.x - windowSize.x / 2, playerPosition.z - windowSize.y / 2);
        setWindowPosition(topLeft);

        Rectanglei region = canvas.getRegion();
        Vector2f worldStart = screenToWorld(new Vector2i(region.minX, region.minY));
        Vector2f worldEnd = screenToWorld(new Vector2i(region.maxX, region.maxY));

        y = (int) playerPosition.y + yDiff;

        for (int z = (int) worldStart.y; z < (int) worldEnd.y; z++) {
            for (int x = (int) worldStart.x; x < (int) worldEnd.x; x++) {
                Vector2i tileStart = worldToScreen(new Vector2f(x, z));
                Vector2i tileEnd = worldToScreen(new Vector2f(x + 1, z + 1));
                Rectanglei screenRegion = JomlUtil.rectangleiFromMinAndSize(tileStart.x, tileStart.y, tileEnd.x - 1,
                    tileEnd.y - 1);

                blockRenderer.renderBlock(canvas, new Vector3i(x, y, z), screenRegion);
                walkableBlockRenderer.renderBlock(canvas, new Vector3i(x, y, z), screenRegion);
                workRenderer.renderBlock(canvas, new Vector3i(x, y, z), screenRegion);
            }
        }

        for (EntityRef entity : CoreRegistry.get(EntityManager.class).getEntitiesWith(LocationComponent.class,
            CharacterComponent.class)) {
            Vector3f worldPos = entity.getComponent(LocationComponent.class).getWorldPosition();
            Vector2i min = worldToScreen(new Vector2f(worldPos.x - 0.4f, worldPos.z - 0.4f));
            Vector2i max = worldToScreen(new Vector2f(worldPos.x + 0.4f, worldPos.z + 0.4f));
            entityRenderer.renderBlock(canvas, entity, new Rectanglei(min, max));
        }

        if (startDrag != null && endDrag != null) {
            canvas.drawLine(startDrag.x, startDrag.y, endDrag.x, startDrag.y, Color.WHITE);
            canvas.drawLine(endDrag.x, startDrag.y, endDrag.x, endDrag.y, Color.WHITE);
            canvas.drawLine(endDrag.x, endDrag.y, startDrag.x, endDrag.y, Color.WHITE);
            canvas.drawLine(startDrag.x, endDrag.y, startDrag.x, startDrag.y, Color.WHITE);
        }
    }
}
