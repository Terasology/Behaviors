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
package org.terasology.minion.trees;

import org.junit.BeforeClass;
import org.junit.Test;
import org.terasology.HeadlessEnvironment;
import org.terasology.asset.AssetManager;
import org.terasology.asset.AssetType;
import org.terasology.asset.AssetUri;
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
public class TestTrees {
    @BeforeClass
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
        BehaviorTree asset = CoreRegistry.get(AssetManager.class).loadAsset(new AssetUri(AssetType.BEHAVIOR, "Behaviors", name), BehaviorTree.class);
        System.out.println(CoreRegistry.get(BehaviorTreeBuilder.class).toJson(asset.getRoot()));
        Actor actor = new Actor(entityRef);
        actor.setDelta(0.5f);
        BehaviorTreeRunner runner = new DefaultBehaviorTreeRunner(asset, actor, null);
        return runner;
    }
}
