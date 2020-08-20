// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion.trees;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.terasology.HeadlessEnvironment;
import org.terasology.assets.AssetType;
import org.terasology.assets.ResourceUrn;
import org.terasology.assets.management.AssetManager;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.behavior.DefaultBehaviorTreeRunner;
import org.terasology.logic.behavior.asset.BehaviorTree;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BehaviorTreeBuilder;
import org.terasology.logic.behavior.core.BehaviorTreeRunner;
import org.terasology.logic.location.LocationComponent;
import org.terasology.naming.Name;
import org.terasology.registry.CoreRegistry;
import org.terasology.rendering.logic.SkeletalMeshComponent;

/**
 * Created by synopia on 22/01/15.
 */
@Disabled("Make tests. possibly, migrate to MTE")
public class TestTrees {
    @BeforeAll
    public static void setup() {
        new HeadlessEnvironment(new Name("Behaviors"));
    }

    @Test
    public void doRemoveBlock() {
        BehaviorTreeRunner runner = load("doRemoveBlock");
        System.out.println(runner.step());
        System.out.println(runner.step());
        System.out.println(runner.step());
        System.out.println(runner.step());
    }

    @Test
    public void doBuildBlock() {
        BehaviorTreeRunner runner = load("doBuildBlock");
        System.out.println(runner.step());
        System.out.println(runner.step());
        System.out.println(runner.step());
        System.out.println(runner.step());
    }

    @Test
    public void doRandomMove() {
        BehaviorTreeRunner runner = load("doRandomMove");
        System.out.println(runner.step());
        System.out.println(runner.step());
        System.out.println(runner.step());
        System.out.println(runner.step());
    }

    @Test
    public void doWalkTo() {
        BehaviorTreeRunner runner = load("doWalkTo");
        System.out.println(runner.step());
        System.out.println(runner.step());
        System.out.println(runner.step());
        System.out.println(runner.step());
    }

    @Test
    public void moveToWork() {
        BehaviorTreeRunner runner = load("moveToWork");
        System.out.println(runner.step());
        System.out.println(runner.step());
        System.out.println(runner.step());
        System.out.println(runner.step());
    }

    @Test
    public void stray() {
        BehaviorTreeRunner runner = load("stray");
        System.out.println(runner.step());
        System.out.println(runner.step());
        System.out.println(runner.step());
        System.out.println(runner.step());
    }

    @Test
    public void worker() {
        BehaviorTreeRunner runner = load("worker");
        System.out.println(runner.step());
        System.out.println(runner.step());
        System.out.println(runner.step());
        System.out.println(runner.step());
    }

    private BehaviorTreeRunner load(String name) {
        EntityRef entityRef = CoreRegistry.get(EntityManager.class).create(new LocationComponent(), new SkeletalMeshComponent());
        BehaviorTree asset = CoreRegistry.get(AssetManager.class).getAsset(new ResourceUrn("Behaviors", name), BehaviorTree.class).get();
        System.out.println(CoreRegistry.get(BehaviorTreeBuilder.class).toJson(asset.getRoot()));
        Actor actor = new Actor(entityRef);
        actor.setDelta(0.5f);
        BehaviorTreeRunner runner = new DefaultBehaviorTreeRunner(asset, actor, null);
        return runner;
    }
}
