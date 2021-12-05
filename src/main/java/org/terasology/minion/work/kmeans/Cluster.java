// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion.work.kmeans;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3ic;
import org.terasology.engine.utilities.random.MersenneRandom;
import org.terasology.engine.utilities.random.Random;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Cluster {
    private Map<Vector3ic, Distance> distances = Maps.newHashMap();
    private List<Cluster> children = Lists.newArrayList();
    private Vector3f position = new Vector3f();
    private DistanceFunction distanceFunction;
    private float maxDistanceBeforeSplit;
    private int splitCount;
    private Random random = new MersenneRandom();
    private boolean dirty = true;
    private int depth;

    public Cluster(float maxDistanceBeforeSplit, int splitCount, int depth, DistanceFunction distanceFunction) {
        this.maxDistanceBeforeSplit = maxDistanceBeforeSplit;
        this.distanceFunction = distanceFunction;
        this.splitCount = splitCount;
        this.depth = depth;
    }

    protected Cluster create() {
        return new Cluster(maxDistanceBeforeSplit, splitCount, depth + 1, distanceFunction);
    }

    public void add(Vector3ic element) {
        distances.put(element, new Distance(Float.MAX_VALUE));
        dirty = true;
    }

    public void add(Vector3ic element, Distance distance) {
        distances.put(element, distance);
        dirty = true;
    }

    public void remove(Vector3ic element) {
        if (distances.remove(element) != null) {
            for (Cluster cluster : children) {
                cluster.remove(element);
            }
        }
        dirty = true;
    }

    public Vector3ic findNearest(Vector3ic target) {
        float minDist = Float.MAX_VALUE;
        Vector3ic nearest = null;
        for (Map.Entry<Vector3ic, Distance> entry : distances.entrySet()) {
            Vector3ic element = entry.getKey();
            float distance = distanceFunction.distance(element, new Vector3f(target));
            if (distance < minDist) {
                nearest = element;
                minDist = distance;
            }
        }
        if (nearest != null) {
            return nearest;
        }
        return null;
    }

    public Cluster findNearestCluster(Vector3ic target) {
        hkMean();

        if (children.size() == 0 || distances.size() == 0) {
            return this;
        }
        float minDist = Float.MAX_VALUE;
        Cluster nearestCluster = null;
        for (Cluster cluster : children) {
            if (cluster.distances.size() == 0 && cluster.children.size() == 0) {
                continue;
            }

            float distance = distanceFunction.distance(target, cluster.getPosition());
            if (distance < minDist) {
                nearestCluster = cluster;
                minDist = distance;
            }
        }
        if (nearestCluster != null) {
            return nearestCluster.findNearestCluster(target);
        }
        return null;
    }

    public boolean hkMean() {
        if (!dirty) {
            return false;
        }

        Stack<Cluster> stack = new Stack<>();
        stack.push(this);

        while (!stack.isEmpty()) {
            Cluster current = stack.pop();
            float maxDistance = 0;
            for (Map.Entry<Vector3ic, Distance> entry : current.distances.entrySet()) {
                float distance = entry.getValue().getDistance();
                if (distance > maxDistance) {
                    maxDistance = distance;
                }
            }

            if (maxDistance > current.maxDistanceBeforeSplit) {
                current.kMean();
                stack.addAll(current.children);
            }
        }
        dirty = false;
        return true;
    }

    public void kMean() {
        int max = Math.min(splitCount, distances.size());
        if (max >= children.size()) {
            for (int i = children.size(); i < max; i++) {
                Cluster cluster = create();
                children.add(cluster);
                cluster.setPosition(new Vector3f(randomAround(position.x), randomAround(position.y), randomAround(position.z)));
            }
        }

        float dist = Float.MAX_VALUE;
        while (dist > 0.1f) {
            dist = iterate();
            updateChildren();
        }
    }

    public List<Cluster> getLeafCluster() {
        return getLeafCluster(new ArrayList<Cluster>());
    }

    public List<Cluster> getLeafCluster(List<Cluster> list) {
        if (getChildren().size() > 0) {
            for (Cluster cluster : getChildren()) {
                cluster.getLeafCluster(list);
            }
            return list;
        } else if (getDistances().size() > 0) {
            list.add(this);
            return list;
        }
        return list;
    }

    private float randomAround(float value) {
        return random.nextFloat(value - maxDistanceBeforeSplit / 2, value + maxDistanceBeforeSplit / 2);
    }

    public float iterate() {
        float totalDistChange = 0;

        clearChildren();
        for (Map.Entry<Vector3ic, Distance> entry : distances.entrySet()) {
            float minDist = Float.MAX_VALUE;
            Cluster nearestCluster = null;
            Vector3ic element = entry.getKey();
            Distance distance = entry.getValue();

            for (Cluster cluster : children) {
                float dist = distanceFunction.distance(element, cluster.getPosition());
                if (dist < minDist) {
                    nearestCluster = cluster;
                    minDist = dist;
                }
            }
            if (nearestCluster != null) {
                totalDistChange += distance.setDistance(minDist);
                nearestCluster.add(element, distance);
            }
        }
        if (distances.size() > 0) {
            totalDistChange /= distances.size();
        } else {
            totalDistChange = 0;
        }

        return totalDistChange;
    }

    public int getElementCount() {
        return distances.size();
    }

    private void updateChildren() {
        for (Cluster cluster : children) {
            cluster.updateCluster();
        }
    }

    private void clearChildren() {
        for (Cluster cluster : children) {
            cluster.clear();
        }
    }

    public void clear() {
        distances.clear();
        dirty = true;
    }

    public List<Cluster> getChildren() {
        return children;
    }

    public void updateCluster() {
        position = new Vector3f();
        for (Vector3ic element : distances.keySet()) {
            position.add(new Vector3f(element));
        }
        position.mul(1.f / distances.size());
    }

    public int getDepth() {
        return depth;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Map<Vector3ic, Distance> getDistances() {
        return distances;
    }

    @Override
    public String toString() {
        return position.toString() + " " + distances.size();
    }

    public static final class Distance {
        private float distance;

        private Distance(float distance) {
            this.distance = distance;
        }

        public float getDistance() {
            return distance;
        }

        public float setDistance(float value) {
            float diff = Math.abs(distance - value);
            distance = value;
            return diff;
        }
    }

    public interface DistanceFunction {
        float distance(Vector3ic element, Vector3fc target);

        float distance(Vector3ic element, Vector3ic target);
    }
}
