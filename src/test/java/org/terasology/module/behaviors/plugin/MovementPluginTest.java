// Copyright 2026 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.plugin;

import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.characters.CharacterMovementComponent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.world.WorldProvider;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression test for #4995: an NPC's real position went NaN, and was later "fixed" to (0, 0, 0), while
 * the in-game pause menu was open. {@code TimeBase#tick} sets {@code gameDelta} to exactly 0 while
 * paused, but nothing gates behavior tree ticking on pause - so {@link MovementPlugin#getDelta} kept
 * running every frame and divided by it unconditionally, producing +/-Infinity that eventually
 * corrupted the entity's actual {@link LocationComponent}.
 */
public class MovementPluginTest {
    private Time time;
    private MovementPlugin plugin;
    private EntityRef entity;
    private LocationComponent location;
    private CharacterMovementComponent movement;

    @BeforeEach
    public void setup() {
        time = Mockito.mock(Time.class);
        WorldProvider world = Mockito.mock(WorldProvider.class);
        // WalkingMovementPlugin doesn't override getDelta - any concrete plugin exercises the same
        // shared implementation in MovementPlugin.
        plugin = new WalkingMovementPlugin(world, time);

        location = new LocationComponent();
        location.setWorldPosition(new Vector3f(0, 0, 0));
        movement = new CharacterMovementComponent();

        entity = Mockito.mock(EntityRef.class);
        Mockito.when(entity.getComponent(LocationComponent.class)).thenReturn(location);
        Mockito.when(entity.getComponent(CharacterMovementComponent.class)).thenReturn(movement);
    }

    @Test
    public void getDeltaIsZeroWhenGameIsPaused() {
        Mockito.when(time.getGameDelta()).thenReturn(0f);

        Vector3f delta = plugin.getDelta(entity, new Vector3f(10, 0, 0));

        assertThat(delta).isEqualTo(new Vector3f());
    }

    @Test
    public void getDeltaIsZeroWhenSpeedMultiplierIsZero() {
        Mockito.when(time.getGameDelta()).thenReturn(0.05f);
        movement.speedMultiplier = 0;

        Vector3f delta = plugin.getDelta(entity, new Vector3f(10, 0, 0));

        assertThat(delta).isEqualTo(new Vector3f());
    }

    @Test
    public void getDeltaIsFiniteUnderNormalConditions() {
        Mockito.when(time.getGameDelta()).thenReturn(0.05f);

        Vector3f delta = plugin.getDelta(entity, new Vector3f(10, 0, 0));

        assertTrue(delta.isFinite());
        assertThat(delta.x()).isGreaterThan(0f);
    }
}
