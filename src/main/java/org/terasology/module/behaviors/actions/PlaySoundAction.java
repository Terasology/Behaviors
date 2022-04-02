// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.actions;

import org.joml.Vector3f;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.gestalt.assets.management.AssetManager;
import org.terasology.engine.audio.AudioEndListener;
import org.terasology.engine.audio.AudioManager;
import org.terasology.engine.audio.StaticSound;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.In;
import org.terasology.gestalt.module.sandbox.API;
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
        // TODO: Temporary fix for injection malfunction, remove once https://github.com/MovingBlocks/Terasology/issues/5004 is fixed.
        if (audioManager == null) {
            audioManager = CoreRegistry.get(AudioManager.class);
        }
        if (assetManager == null) {
            assetManager = CoreRegistry.get(AssetManager.class);
        }

        if (soundUrn != null) {
            StaticSound snd = assetManager.getAsset(soundUrn, StaticSound.class).orElse(null);

            if (snd != null) {
                if (actor.hasComponent(LocationComponent.class)) {
                    Vector3f worldPosition = actor.getComponent(LocationComponent.class).getWorldPosition(new Vector3f());
                    audioManager.playSound(snd, worldPosition, volume, AudioManager.PRIORITY_NORMAL, createEndListener(actor));

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
