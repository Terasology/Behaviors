// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.systems;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.core.SimpleUri;
import org.terasology.engine.core.Time;
import org.terasology.engine.core.Uri;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.engine.registry.Share;
import org.terasology.engine.world.WorldProvider;
import org.terasology.module.behaviors.components.MinionMoveComponent;
import org.terasology.module.behaviors.plugin.CompositeMovementPlugin;
import org.terasology.module.behaviors.plugin.FlyingMovementPlugin;
import org.terasology.module.behaviors.plugin.LeapingMovementPlugin;
import org.terasology.module.behaviors.plugin.MovementPlugin;
import org.terasology.module.behaviors.plugin.SwimmingMovementPlugin;
import org.terasology.module.behaviors.plugin.WalkingMovementPlugin;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Share(PluginSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class PluginSystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(PluginSystem.class);

    @In
    private WorldProvider worldProvider;

    @In
    private Time time;

    private final Map<Uri, Function<EntityRef, MovementPlugin>> registeredPlugins = Maps.newHashMap();

    @Override
    public void initialise() {
        super.initialise();
        registerMovementPlugin("walking", (entity) -> new WalkingMovementPlugin(worldProvider, time));
        registerMovementPlugin("leaping", (entity) -> new LeapingMovementPlugin(worldProvider, time));
        registerMovementPlugin("flying", (entity) -> new FlyingMovementPlugin(worldProvider, time));
        registerMovementPlugin("swimming", (entity) -> new SwimmingMovementPlugin(worldProvider, time));
    }

    public void registerMovementPlugin(String name, Function<EntityRef, MovementPlugin> supplier) {
        SimpleUri uri = new SimpleUri(name);
        if (uri.getModuleName().isEmpty()) {
            uri = new SimpleUri("FlexibleMovement", name);
        }

        if (!uri.isValid()) {
            logger.error("Not registering invalid movement plugin URI: {}", uri);
            return;
        }

        if (registeredPlugins.containsKey(uri)) {
            logger.warn("MovementPlugin {} already registered, overwriting", uri);
        }

        registeredPlugins.put(uri, supplier);
    }

    public MovementPlugin getMovementPlugin(EntityRef entity) {
        List<MovementPlugin> plugins = Lists.newArrayList();
        MinionMoveComponent component = entity.getComponent(MinionMoveComponent.class);
        for (String movementType : component.movementTypes) {

            // if the module name is omitted, assume it's from the base module
            SimpleUri uri = new SimpleUri(movementType);
            if (uri.getModuleName().isEmpty()) {
                uri = new SimpleUri("FlexibleMovement", movementType);
            }

            if (!uri.isValid() || !registeredPlugins.containsKey(uri)) {
                logger.warn("Unknown or invalid MovementPlugin requested: {}", uri);
            }

            MovementPlugin newPlugin = registeredPlugins.get(uri).apply(entity);
            plugins.add(newPlugin);

        }
        return new CompositeMovementPlugin(worldProvider, time, plugins);
    }
}
