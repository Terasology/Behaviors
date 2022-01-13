// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.plugin;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.characters.CharacterMoveInputEvent;
import org.terasology.engine.logic.characters.CharacterMovementComponent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.world.WorldProvider;
import org.terasology.flexiblepathfinding.plugins.JPSPlugin;
import org.terasology.math.TeraMath;

public abstract class MovementPlugin {
    private WorldProvider world;
    private Time time;

    public MovementPlugin(WorldProvider world, Time time) {
        this.time = time;
        this.world = world;
    }

    // needed to instantiate plugins by name in the movement system
    public MovementPlugin() {
        //do nothing
    }

    public static String getName() {
        return "movement";
    }

    public abstract JPSPlugin getJpsPlugin(EntityRef entity);

    public abstract CharacterMoveInputEvent move(EntityRef entity, Vector3fc dest, int sequence);

    public WorldProvider getWorld() {
        return world;
    }

    public void setWorld(WorldProvider world) {
        this.world = world;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public float getYaw(Vector3fc delta) {
        return ((float) Math.atan2(delta.x(), delta.z())) * TeraMath.RAD_TO_DEG + 180.0f;
    }

    public Vector3f getDelta(EntityRef entity, Vector3fc dest) {
        LocationComponent location = entity.getComponent(LocationComponent.class);
        CharacterMovementComponent movement = entity.getComponent(CharacterMovementComponent.class);

        Vector3f delta = dest.sub(location.getWorldPosition(new Vector3f()), new Vector3f());
        delta.div(movement.speedMultiplier).div(getTime().getGameDelta());
        return delta;
    }
}
