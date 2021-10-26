// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion.work;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.core.SimpleUri;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.engine.registry.Share;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.nui.databinding.ReadOnlyBinding;
import org.terasology.nui.itemRendering.StringTextRenderer;
import org.terasology.nui.properties.OneOfProviderFactory;

import java.util.List;
import java.util.Map;

@RegisterSystem
@Share(value = WorkFactory.class)
public class WorkFactory extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(WorkFactory.class);


    @In
    private OneOfProviderFactory providerFactory;

    @In
    private BlockManager blockManager;

    private Map<SimpleUri, Work> workRegistry = Maps.newHashMap();
    private List<Work> works = Lists.newArrayList();
    // TODO REAL IDLE
    private SimpleUri idle = new SimpleUri("Pathfinding:idle");

    /* Kept to pass to RemoveBlock nodes if needed - so they don't
     * have to fetch their own Air block from somewhere
     */
    private Block air;

    public void register(Work work) {
        workRegistry.put(work.getUri(), work);
        works.add(work);
    }

    public List<Work> getWorks() {
        return works;
    }

    public Work getWork(String uri) {
        return workRegistry.get(new SimpleUri(uri));
    }

    public Work getWork(EntityRef workItem) {
        WorkComponent workComponent = workItem.getComponent(WorkComponent.class);
        if (workComponent != null) {
            return workRegistry.get(workComponent.getUri());
        }
        return workRegistry.get(idle);
    }

    @Override
    public void initialise() {
        logger.info("Initialize WorkFactory");
        providerFactory.register("work", new ReadOnlyBinding<List<String>>() {
            @Override
            public List<String> get() {
                List<String> result = Lists.newArrayList();
                for (Work work : works) {
                    result.add(work.getUri().toString());
                }
                return result;
            }
        }, new StringTextRenderer<String>() {
            @Override
            public String getString(String value) {
                return value.substring(value.indexOf(':') + 1);
            }
        });


    }

    @Override
    public void shutdown() {
    }

    public Block getAir() {
        if (air == null) {
            air = blockManager.getBlock(BlockManager.AIR_ID);
        }
        return air;
    }
}
