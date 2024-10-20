// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.actions;

import org.terasology.engine.registry.CoreRegistry;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.gestalt.assets.management.AssetManager;
import org.terasology.engine.audio.AudioEndListener;
import org.terasology.engine.audio.AudioManager;
import org.terasology.engine.audio.StreamingSound;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.registry.In;
import org.terasology.context.annotation.API;
import org.terasology.module.behaviors.systems.PluginSystem;
import org.terasology.nui.properties.OneOf;
import org.terasology.nui.properties.Range;

/**
 * Plays music in background. Return SUCCESS when music ends.
 */
@API
@BehaviorAction(name = "music")
public class PlayMusicAction extends BaseAction {
    @OneOf.Provider(name = "music")
    private ResourceUrn musicUrn;
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

        if (musicUrn != null) {
            StreamingSound snd = assetManager.getAsset(musicUrn, StreamingSound.class).orElse(null);

            if (snd != null) {
                audioManager.playMusic(snd, volume, createEndListener(actor));
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
        return (boolean interrupted) -> actor.setValue(getId(), false);
    }

}
