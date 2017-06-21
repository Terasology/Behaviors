/*
 * Copyright 2015 MovingBlocks
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
package org.terasology.minion.work;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.math.geom.Vector3i;
import org.terasology.math.geom.Vector3f;
import org.terasology.minion.work.kmeans.Cluster;
import org.terasology.navgraph.WalkableBlock;

import java.util.Map;
import java.util.Set;

/**
 * Created by synopia on 08.02.14.
 */
public class WorkType {
    private final Work work;
    private final Set<EntityRef> openWork = Sets.newHashSet();
    private final Set<EntityRef> requestableWork = Sets.newHashSet();
    private final Map<Vector3i, EntityRef> mapping = Maps.newHashMap();
    private Cluster cluster;

    public WorkType(Work work) {
        this.work = work;
        cluster = new Cluster(8, 4, 1, new Cluster.DistanceFunction() {
            @Override
            public float distance(Vector3i element, Vector3i target) {
                EntityRef workEntity = mapping.get(element);
                if (workEntity != null && requestableWork.contains(workEntity)) {
                    return (float) element.distance(target);
                }
                return Float.MAX_VALUE;
            }

            @Override
            public float distance(Vector3i element, Vector3f target) {
                Vector3f diff = element.toVector3f();
                diff.sub(target);
                return diff.length();
            }
        });
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int remaining = 10;
        for (EntityRef workEntity : openWork) {
            remaining--;
            if (remaining == 0) {
                break;
            }
            sb.append(workEntity);
            sb.append(", ");
        }
        return work.getUri() + ": open=" + openWork.size() + " requestable=" + requestableWork.size() + " targets=" + mapping.size() + " " + sb;
    }

    public void update(EntityRef workEntity) {
        WorkTargetComponent workComponent = workEntity.getComponent(WorkTargetComponent.class);
        if (workComponent != null && work.isAssignable(workEntity)) {
            openWork.add(workEntity);
            if (workComponent.assignedMinion == null && workComponent.isRequestable(workEntity)) {
                requestableWork.add(workEntity);
                for (WalkableBlock block : work.getTargetPositions(workEntity)) {
                    cluster.add(block.getBlockPosition());
                    mapping.put(block.getBlockPosition(), workEntity);
                }
            } else {
                remove(workEntity);
            }
        } else {
            remove(workEntity);
        }
    }

    public Vector3i findNearestTarget(Vector3i position) {
        return cluster.findNearest(position);
    }

    public EntityRef getWorkForTarget(Vector3i position) {
        return mapping.get(position);
    }

    public void remove(EntityRef workEntity) {
        if (workEntity != null) {
            openWork.remove(workEntity);
            requestableWork.remove(workEntity);
            for (WalkableBlock block : work.getTargetPositions(workEntity)) {
                cluster.remove(block.getBlockPosition());
                mapping.remove(block.getBlockPosition());
            }
        }
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void removeRequestable(EntityRef workEntity) {
        requestableWork.remove(workEntity);
        for (WalkableBlock block : work.getTargetPositions(workEntity)) {
            cluster.remove(block.getBlockPosition());
            mapping.remove(block.getBlockPosition());
        }
    }

    public Cluster findNearestCluster(Vector3i position) {
        return cluster.findNearestCluster(position);
    }
}
