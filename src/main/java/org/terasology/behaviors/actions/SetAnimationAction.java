// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.actions;

import org.terasology.engine.ComponentFieldUri;
import org.terasology.gestalt.module.sandbox.API;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.nui.properties.OneOf;
import org.terasology.rendering.assets.animation.MeshAnimation;
import org.terasology.rendering.logic.SkeletalMeshComponent;

import java.util.List;
import java.util.Random;

/**
 * Plays a animation from a animation set and sets the animation pool to pick animation to play from.
 * <p/>
 * The node returns SUCCESS after the animation is played completely.
 */
@API
@BehaviorAction(name = "animation")
public class SetAnimationAction extends BaseAction {
    @OneOf.Provider(name = "animations")
    private ComponentFieldUri play;

    @OneOf.Provider(name = "animations")
    private ComponentFieldUri loop;

    private transient Random random;

    public SetAnimationAction() {
        random = new Random();
    }

    @Override
    public void construct(Actor actor) {
        SkeletalMeshComponent skeletalMesh = actor.getComponent(SkeletalMeshComponent.class);
        if (play != null) {
            List<?> animationListToPlay = (List<?>) actor.getComponentField(play);
            if (animationListToPlay != null) {
                skeletalMesh.animation = (MeshAnimation) animationListToPlay.get(
                        random.nextInt(animationListToPlay.size()));
            } else {
                // ignore error, effect is visible
                skeletalMesh.animation = null;
            }
        }
        if (loop != null) {
            skeletalMesh.animationPool.clear();
            List<?> animationListToLoop = (List<?>) actor.getComponentField(loop);
            if (animationListToLoop != null) {
                for (Object object : animationListToLoop) {
                    skeletalMesh.animationPool.add((MeshAnimation) object);
                }
            }
        }
        skeletalMesh.loop = true;
        actor.save(skeletalMesh);
    }

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        return BehaviorState.SUCCESS;
    }
}
