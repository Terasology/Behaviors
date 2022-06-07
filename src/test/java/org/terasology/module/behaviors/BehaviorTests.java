// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.module.behaviors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.integrationenvironment.jupiter.Dependencies;
import org.terasology.engine.integrationenvironment.jupiter.MTEExtension;
import org.terasology.engine.logic.behavior.BehaviorComponent;
import org.terasology.engine.logic.behavior.asset.BehaviorTree;
import org.terasology.engine.registry.In;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.gestalt.assets.management.AssetManager;

import java.util.Optional;
import java.util.stream.Stream;


@ExtendWith(MTEExtension.class)
@Dependencies("Behaviors")
@Tag("MteTest")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BehaviorTests {

    @In
    AssetManager assetManager;
    @In
    EntityManager entityManager;

    public Stream<Arguments> behaviors() {
        return assetManager.getAvailableAssets(BehaviorTree.class)
                .stream()
                .map(Arguments::of);
    }


    public Stream<Arguments> prefabs() {
        return assetManager.getAvailableAssets(Prefab.class)
                .stream()
                .map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("behaviors")
    void behaviorsTest(ResourceUrn urn) {
        Optional<BehaviorTree> tree = Assertions.assertDoesNotThrow(() ->
                assetManager.getAsset(urn, BehaviorTree.class), "Asset manager should be able to load behavior tree asset '" + urn + "'.");

        Assertions.assertTrue(tree.isPresent(), "Loaded behavior tree asset '" + urn + "' should not be empty.");
        Assertions.assertNotNull(tree.get().getData(), "Behavior '" + urn + "' should have non-null data field.");
        Assertions.assertNotNull(tree.get().getRoot(), "Behavior '" + urn + "' should have non-null roo node.");
    }

    @ParameterizedTest
    @MethodSource("prefabs")
    void prefabTest(ResourceUrn urn) {
        Optional<Prefab> prefab = Assertions.assertDoesNotThrow(() ->
                assetManager.getAsset(urn, Prefab.class));
        Assertions.assertTrue(prefab.isPresent());

        if (prefab.get().hasComponent(BehaviorComponent.class)) {
            BehaviorComponent component = prefab.get().getComponent(BehaviorComponent.class);
            Assertions.assertNotNull(component.tree, "Prefab '" + urn + "' with BehaviorComponent references valid behavior tree.");
            EntityRef ref = entityManager.create(prefab.get());

        } else {
            Assumptions.assumeTrue(false, "prefab haven't behaviour");
        }
    }

}
