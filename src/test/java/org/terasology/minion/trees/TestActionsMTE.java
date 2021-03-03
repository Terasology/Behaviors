// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.minion.trees;


import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.terasology.assets.ResourceUrn;
import org.terasology.assets.management.AssetManager;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.logic.behavior.BehaviorSystem;
import org.terasology.logic.behavior.asset.BehaviorTree;
import org.terasology.logic.behavior.core.BehaviorTreeBuilder;
import org.terasology.logic.behavior.nui.BehaviorEditor;
import org.terasology.logic.behavior.nui.BehaviorNodeFactory;
import org.terasology.moduletestingenvironment.ModuleTestingEnvironment;
import org.terasology.nui.properties.OneOfProviderFactory;
import org.terasology.registry.InjectionHelper;

import java.util.Set;

class TestActionsMTE extends ModuleTestingEnvironment {

    protected EntityManager entityManager;

    public Set<String> getDependencies() {
        return Sets.newHashSet("engine", "ModuleTestingEnvironment", "Behaviors");
    }

    @BeforeEach
    void beforeMyModuleTests() {
        entityManager = getHostContext().get(EntityManager.class);
        runUntil(1000, () -> {
            return true;
        });
    }


    @Test
    void moveTo() {

        BehaviorNodeFactory nodeFactory = new BehaviorNodeFactory();
        BehaviorSystem behaviorSystem = new BehaviorSystem();

        getHostContext().put(OneOfProviderFactory.class, new OneOfProviderFactory());
        getHostContext().put(BehaviorNodeFactory.class, nodeFactory);
        InjectionHelper.inject(nodeFactory, getHostContext());
        getHostContext().put(BehaviorSystem.class, behaviorSystem);
        InjectionHelper.inject(behaviorSystem, getHostContext());
        nodeFactory.refreshLibrary();

        BehaviorTreeBuilder builder = new BehaviorTreeBuilder();
        BehaviorEditor editor = new BehaviorEditor();
        editor.initialize(getHostContext());

        editor.setTree(getHostContext().get(AssetManager.class).getAsset(new ResourceUrn("Behaviors", "worker"), BehaviorTree.class).get());
        editor.createNode(nodeFactory.getNodeComponent(builder.fromJson("move_to")));
    }
}
