// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.components;

import com.google.common.collect.Lists;
import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.rendering.assets.animation.MeshAnimation;

import java.util.List;

/**
 * The components gets currently only used as container for the melee attack animation.
 */
public class MeleeAttackComponent implements Component {
    /**
     * A pool of attack animations. The animations of the pool will be picked by random. The result is a randomized
     * animation loop. The same animation can be put multiple times in the pool, so that it will be chosen more
     * frequently.
     */
    public List<MeshAnimation> animationPool = Lists.newArrayList();

}
