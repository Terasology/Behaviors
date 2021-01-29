// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion.trees;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.terasology.HeadlessEnvironment;
import org.terasology.assets.ResourceUrn;
import org.terasology.assets.management.AssetManager;
import org.terasology.logic.behavior.BehaviorSystem;
import org.terasology.logic.behavior.asset.BehaviorTree;
import org.terasology.logic.behavior.core.BehaviorTreeBuilder;
import org.terasology.logic.behavior.nui.BehaviorEditor;
import org.terasology.logic.behavior.nui.BehaviorNodeFactory;
import org.terasology.naming.Name;
import org.terasology.nui.properties.OneOfProviderFactory;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.InjectionHelper;

public class TestActions {
    @Test
    @Disabled("Fix it, rewrite on MTE")
    public void moveTo() {
        new HeadlessEnvironment(new Name("Behaviors"));

        BehaviorNodeFactory nodeFactory = new BehaviorNodeFactory();
        BehaviorSystem behaviorSystem = new BehaviorSystem();

        CoreRegistry.put(OneOfProviderFactory.class, new OneOfProviderFactory());
        CoreRegistry.put(BehaviorNodeFactory.class, nodeFactory);
        InjectionHelper.inject(nodeFactory);
        CoreRegistry.put(BehaviorSystem.class, behaviorSystem);
        InjectionHelper.inject(behaviorSystem);
        nodeFactory.refreshLibrary();

        BehaviorTreeBuilder builder = new BehaviorTreeBuilder();
        BehaviorEditor editor = new BehaviorEditor();
        editor.setTree(CoreRegistry.get(AssetManager.class).getAsset(new ResourceUrn("Behaviors", "worker"), BehaviorTree.class).get());
        editor.createNode(nodeFactory.getNodeComponent(builder.fromJson("move_to")));
    }
}
