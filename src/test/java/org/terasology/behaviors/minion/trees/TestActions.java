// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.minion.trees;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.terasology.HeadlessEnvironment;
import org.terasology.engine.logic.behavior.BehaviorSystem;
import org.terasology.engine.logic.behavior.asset.BehaviorTree;
import org.terasology.engine.logic.behavior.core.BehaviorTreeBuilder;
import org.terasology.engine.logic.behavior.nui.BehaviorEditor;
import org.terasology.engine.logic.behavior.nui.BehaviorNodeFactory;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.registry.InjectionHelper;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.gestalt.assets.management.AssetManager;
import org.terasology.gestalt.naming.Name;
import org.terasology.nui.properties.OneOfProviderFactory;

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
        editor.setTree(CoreRegistry.get(AssetManager.class).getAsset(new ResourceUrn("Behaviors", "worker"),
                BehaviorTree.class).get());
        editor.createNode(nodeFactory.getNodeComponent(builder.fromJson("move_to")));
    }
}
