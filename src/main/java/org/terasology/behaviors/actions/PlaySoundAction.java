// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.actions;

import org.terasology.assets.ResourceUrn;
import org.terasology.assets.management.AssetManager;
import org.terasology.audio.AudioEndListener;
import org.terasology.audio.AudioManager;
import org.terasology.audio.StaticSound;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.JomlUtil;
import org.terasology.math.geom.Vector3f;
import org.terasology.module.sandbox.API;
import org.terasology.registry.In;
import org.terasology.nui.properties.OneOf;
import org.terasology.nui.properties.Range;

/**
 * Plays a sound. Return SUCCESS when sound ends.
 */
@API
@BehaviorAction(name = "sound")
public class PlaySoundAction extends BaseAction {
    @OneOf.Provider(name = "sounds")
    private ResourceUrn soundUrn;
    @Range(min = 0, max = 1)
    private float volume;
    @In
    private transient AudioManager audioManager;

    @In
    private transient AssetManager assetManager;

    @Override
    public void construct(Actor actor) {
        if (soundUrn != null) {

            StaticSound snd = assetManager.getAsset(soundUrn, StaticSound.class).orElse(null);


            if (snd != null) {
                if (actor.hasComponent(LocationComponent.class)) {
                    Vector3f worldPosition = actor.getComponent(LocationComponent.class).getWorldPosition();
                    audioManager.playSound(snd, JomlUtil.from(worldPosition), volume, AudioManager.PRIORITY_NORMAL, createEndListener(actor));
                } else {
                    audioManager.playSound(snd, new org.joml.Vector3f(), volume, AudioManager.PRIORITY_NORMAL, createEndListener(actor));
                }
                actor.setValue(getId(), true);
            }
        }
    }

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        Boolean playing = actor.getValue(getId());
        if (playing == null || !playing) {
            return BehaviorState.SUCCESS;
        }
        return BehaviorState.RUNNING;
    }

    private AudioEndListener createEndListener(final Actor actor) {
        return interrupted -> actor.setValue(getId(), false);

    }

}
