// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion.work;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3ic;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.minion.work.kmeans.Cluster;

import java.util.Map;
import java.util.Set;

public class WorkType {
    private final Work work;
    private final Set<EntityRef> openWork = Sets.newHashSet();
    private final Set<EntityRef> requestableWork = Sets.newHashSet();
    private final Map<Vector3ic, EntityRef> mapping = Maps.newHashMap();
    private Cluster cluster;

    public WorkType(Work work) {
        this.work = work;
        cluster = new Cluster(8, 4, 1, new Cluster.DistanceFunction() {
            @Override
            public float distance(Vector3ic element, Vector3ic target) {
                EntityRef workEntity = mapping.get(element);
                if (workEntity != null && requestableWork.contains(workEntity)) {
                    return (float) element.distance(target);
                }
                return Float.MAX_VALUE;
            }

            @Override
            public float distance(Vector3ic element, Vector3fc target) {
                Vector3f diff = new Vector3f(element);
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
                for (Vector3ic block : work.getTargetPositions(workEntity)) {
                    cluster.add(block);
                    mapping.put(block, workEntity);
                }
            } else {
                remove(workEntity);
            }
        } else {
            remove(workEntity);
        }
    }

    public Vector3ic findNearestTarget(Vector3ic position) {
        return cluster.findNearest(position);
    }

    public EntityRef getWorkForTarget(Vector3ic position) {
        return mapping.get(position);
    }

    public void remove(EntityRef workEntity) {
        if (workEntity != null) {
            openWork.remove(workEntity);
            requestableWork.remove(workEntity);
            for (Vector3ic block : work.getTargetPositions(workEntity)) {
                cluster.remove(block);
                mapping.remove(block);
            }
        }
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void removeRequestable(EntityRef workEntity) {
        if (!workEntity.exists()) {
            return;
        }
        requestableWork.remove(workEntity);
        for (Vector3ic block : work.getTargetPositions(workEntity)) {
            cluster.remove(block);
            mapping.remove(block);
        }
    }

    public Cluster findNearestCluster(Vector3ic position) {
        return cluster.findNearestCluster(position);
    }
}
