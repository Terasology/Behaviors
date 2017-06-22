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

import org.junit.Test;
import org.terasology.HeadlessEnvironment;
import org.terasology.assets.AssetType;
import org.terasology.assets.ResourceUrn;
import org.terasology.assets.management.AssetManager;
import org.terasology.logic.behavior.BehaviorSystem;
import org.terasology.logic.behavior.asset.BehaviorTree;
import org.terasology.logic.behavior.core.BehaviorTreeBuilder;
import org.terasology.logic.behavior.nui.BehaviorEditor;
import org.terasology.logic.behavior.nui.BehaviorNodeFactory;
import org.terasology.naming.Name;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.InjectionHelper;
import org.terasology.rendering.nui.properties.OneOfProviderFactory;

public class TestActions {
    @Test
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
