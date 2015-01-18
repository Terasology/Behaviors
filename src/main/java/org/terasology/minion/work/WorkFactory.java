/*
 * Copyright 2014 MovingBlocks
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
package org.terasology.minion.work;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.SimpleUri;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.rendering.nui.databinding.ReadOnlyBinding;
import org.terasology.rendering.nui.itemRendering.StringTextRenderer;
import org.terasology.rendering.nui.properties.OneOfProviderFactory;

import java.util.List;
import java.util.Map;

/**
 * @author synopia
 */
@RegisterSystem
@Share(value = WorkFactory.class)
public class WorkFactory extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(WorkFactory.class);

    @In
    private OneOfProviderFactory providerFactory;

    private Map<SimpleUri, Work> workRegistry = Maps.newHashMap();
    private List<Work> works = Lists.newArrayList();
    private SimpleUri idle = new SimpleUri("Pathfinding:idle");

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
                }
        );
    }

    @Override
    public void shutdown() {
    }
}
