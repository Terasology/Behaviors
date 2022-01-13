// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblemovement.debug;

import org.terasology.nui.Canvas;
import org.terasology.nui.layouts.FlowLayout;

public class FlexibleMovementDebugLayout extends FlowLayout {
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        for(EntityRef entity : CoreRegistry.get(EntityManager.class).getEntitiesWith(FlexibleMovementComponent.class)) {
//            Matrix4f matrix = new Matrix4f();
//            Vector3f position = entity.getComponent(LocationComponent.class).getWorldPosition();
//            matrix.set(new Quat4d(position.x, position.y, position.z, 1.0));
//            matrix.mul(CoreRegistry.get(WorldRenderer.class).getActiveCamera().getViewProjectionMatrix());
//
//            Vector2i screenPos = new Vector2i(matrix.getM00(), matrix.getM01());
//
//            Rect2i region = Rect2i.createFromMinAndSize(screenPos.x, screenPos.y, 100, 100);
//            canvas.drawText(entity.getParentPrefab().getName(), region);
//        }
    }
}
