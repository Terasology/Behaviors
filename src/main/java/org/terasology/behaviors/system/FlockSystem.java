/*
 * Copyright 2019 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.behaviors.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.registry.In;
import org.terasology.behaviors.components.FlockComponent;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class FlockSystem extends BaseComponentSystem implements UpdateSubscriberSystem {

    private static final Logger logger = LoggerFactory.getLogger(FindNearbyPlayersSystem.class);
    private transient Random random = new Random();

    @In
    private EntityManager entityManager;

    @Override
    public void update(float delta) {

        Set<EntityRef> flock = new HashSet<>();
        Vector3f flockCentre = Vector3f.zero();
        Vector3f flockAvoid = Vector3f.zero();
        Vector3f basePosition = Vector3f.zero();
        Vector3f baseDirection = Vector3f.zero();
        float neighbourRadius = 1000f;
        float groupSpeed = 0.1f;

        for (EntityRef entity : entityManager.getEntitiesWith(FlockComponent.class)) {
            flock.add(entity);
        }

        int flockSize = flock.size();
        int baseEntityIndex = random.nextInt(flockSize - 1);
        EntityRef baseEntity = (EntityRef)(flock.toArray()[baseEntityIndex]);
        basePosition = baseEntity.getComponent(LocationComponent.class).getWorldPosition();
        baseDirection = baseEntity.getComponent(LocationComponent.class).getWorldDirection();
        neighbourRadius = baseEntity.getComponent(FlockComponent.class).searchRadius;

        for(EntityRef memberCandidate : flock) {
            Vector3f memberPosition = memberCandidate.getComponent(LocationComponent.class).getWorldPosition();
            float distance = Vector3f.distance(memberPosition,baseDirection);
            if(distance <= neighbourRadius) {
                flockCentre.setX(flockCentre.getX() + memberPosition.getX());
                flockCentre.setY(flockCentre.getY() + memberPosition.getY());
                flockCentre.setZ(flockCentre.getZ() + memberPosition.getZ());
                if(distance < 1f) {
                    flockAvoid.setX(flockAvoid.getX() + basePosition.getX() - memberPosition.getX());
                    flockAvoid.setY(flockAvoid.getY() + basePosition.getY() - memberPosition.getY());
                    flockAvoid.setZ(flockAvoid.getZ() + basePosition.getZ() - memberPosition.getZ());
                }
                groupSpeed += memberCandidate.getComponent(FlockComponent.class).speed;
            }
        }

        flockCentre.setX(flockCentre.getX()/flockSize);
        flockCentre.setY(flockCentre.getY()/flockSize);
        flockCentre.setZ(flockCentre.getZ()/flockSize);

        for(EntityRef member : flock) {
            FlockComponent flockComponent  = member.getComponent(FlockComponent.class);
            flockComponent.speed = groupSpeed;
            flockComponent.flockCentre = flockCentre;
            flockComponent.flockAvoid = flockAvoid;
            member.saveComponent(flockComponent);
        }

    }
}
