// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.minion.trees;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.terasology.assets.ResourceUrn;
import org.terasology.assets.management.AssetManager;
import org.terasology.context.Context;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.logic.behavior.BehaviorSystem;
import org.terasology.logic.behavior.asset.BehaviorTree;
import org.terasology.logic.behavior.core.BehaviorTreeBuilder;
import org.terasology.logic.behavior.nui.BehaviorEditor;
import org.terasology.logic.behavior.nui.BehaviorNodeFactory;
import org.terasology.moduletestingenvironment.MTEExtension;
import org.terasology.moduletestingenvironment.ModuleTestingHelper;
import org.terasology.moduletestingenvironment.extension.Dependencies;
import org.terasology.nui.properties.OneOfProviderFactory;
import org.terasology.registry.In;
import org.terasology.registry.InjectionHelper;

@ExtendWith(MTEExtension.class)
@Dependencies({"engine", "ModuleTestingEnvironment", "Behaviors"})
class TestActionsMTE {

    @In
    private ModuleTestingHelper helper;
    @In
    private EntityManager entityManager;
    @In
    private AssetManager assetManager;

    private BehaviorNodeFactory nodeFactory;

    @BeforeEach
    void setup() {
        nodeFactory = new BehaviorNodeFactory();
        BehaviorSystem behaviorSystem = new BehaviorSystem();
        Context hostContext = helper.getHostContext();

        hostContext.put(OneOfProviderFactory.class, new OneOfProviderFactory());
        hostContext.put(BehaviorNodeFactory.class, nodeFactory);
        InjectionHelper.inject(nodeFactory, hostContext);
        hostContext.put(BehaviorSystem.class, behaviorSystem);
        InjectionHelper.inject(behaviorSystem, hostContext);
        nodeFactory.refreshLibrary();
    }

    @Test
    void moveTo() {

        BehaviorTreeBuilder builder = new BehaviorTreeBuilder();
        BehaviorEditor editor = new BehaviorEditor();
        editor.initialize(helper.getHostContext());

        editor.setTree(assetManager.getAsset(new ResourceUrn("Behaviors", "worker"), BehaviorTree.class).get());
        editor.createNode(nodeFactory.getNodeComponent(builder.fromJson("move_to")));
    }
}
