// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.actions;

import org.terasology.audio.AudioEndListener;
import org.terasology.audio.AudioManager;
import org.terasology.audio.StreamingSound;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.gestalt.assets.management.AssetManager;
import org.terasology.gestalt.module.sandbox.API;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.nui.properties.OneOf;
import org.terasology.nui.properties.Range;
import org.terasology.registry.In;

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
