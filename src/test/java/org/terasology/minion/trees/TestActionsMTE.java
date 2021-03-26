// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.minion.trees;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.terasology.assets.ResourceUrn;
import org.terasology.assets.management.AssetManager;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.logic.behavior.asset.BehaviorTree;
import org.terasology.engine.logic.behavior.core.BehaviorTreeBuilder;
import org.terasology.engine.logic.behavior.nui.BehaviorEditor;
import org.terasology.engine.logic.behavior.nui.BehaviorNodeComponent;
import org.terasology.engine.logic.behavior.nui.BehaviorNodeFactory;
import org.terasology.engine.logic.behavior.nui.RenderableNode;
import org.terasology.engine.registry.In;
import org.terasology.moduletestingenvironment.MTEExtension;
import org.terasology.moduletestingenvironment.ModuleTestingHelper;
import org.terasology.moduletestingenvironment.extension.Dependencies;

import java.util.Optional;

@ExtendWith(MTEExtension.class)
@Dependencies({"engine", "ModuleTestingEnvironment", "Behaviors"})
@Tag("MteTest")
class TestActionsMTE {

    @In
    private ModuleTestingHelper helper;
    @In
    private EntityManager entityManager;
    @In
    private AssetManager assetManager;
    @In
    private BehaviorNodeFactory nodeFactory;

    @Test
    void moveTo() {
        BehaviorTreeBuilder builder = new BehaviorTreeBuilder();
        BehaviorEditor editor = new BehaviorEditor();
        editor.initialize(helper.getHostContext());

        ResourceUrn assetUrn = new ResourceUrn("Behaviors", "worker");
        Optional<BehaviorTree> asset = assetManager.getAsset(assetUrn, BehaviorTree.class);
        Assertions.assertTrue(asset.isPresent(), "Should present: " + assetUrn);

        editor.setTree(asset.get());

        BehaviorNodeComponent nodeComponent = nodeFactory.getNodeComponent(builder.fromJson("move_to"));
        Assertions.assertNotEquals(BehaviorNodeComponent.DEFAULT, nodeComponent, "NodeComponent should present");

        RenderableNode renderableNode = editor.createNode(nodeComponent);
        Assertions.assertNotNull(renderableNode, "RenderableNode should be present");
    }
}
