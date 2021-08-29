// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion.work;

import com.google.common.collect.Maps;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.core.GameScheduler;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.BeforeRemoveComponent;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnAddedComponent;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnChangedComponent;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.engine.logic.characters.CharacterComponent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.logic.selection.ApplyBlockSelectionEvent;
import org.terasology.engine.registry.In;
import org.terasology.engine.registry.Share;
import org.terasology.engine.world.BlockEntityRegistry;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.minion.move.MinionMoveComponent;
import org.terasology.minion.work.kmeans.Cluster;
import org.terasology.navgraph.NavGraphChanged;
import org.terasology.navgraph.WalkableBlock;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 *
 */
@RegisterSystem
@Share(value = WorkBoard.class)
public class WorkBoard extends BaseComponentSystem implements UpdateSubscriberSystem {
    private static final Logger logger = LoggerFactory.getLogger(WorkBoard.class);
    private final Map<Work, WorkType> workTypes = Maps.newHashMap();

    @In
    private BlockEntityRegistry blockEntityRegistry;
    @In
    private EntityManager entityManager;
    @In
    private WorkFactory workFactory;

    private float cooldown = 5;

    @Override
    public void update(float delta) {
        cooldown -= delta;
        if (cooldown < 0) {
            for (WorkType type : workTypes.values()) {
                logger.info(type.toString());
            }
            cooldown = 5;
        }
    }

    @Override
    public void initialise() {
        logger.info("Initialize WorkBoard");
    }

    @ReceiveEvent
    public void onNavGraph(NavGraphChanged event, EntityRef entityRef) {
        Flux.fromIterable(entityManager.getEntitiesWith(WorkTargetComponent.class))
                .subscribeOn(GameScheduler.parallel())
                .subscribe(this::updateTarget);
    }

    private void updateTarget(EntityRef target) {
        WorkTargetComponent component = target.getComponent(WorkTargetComponent.class);
        if (component != null) {
            WorkType workType = getWorkType(component.getWork());
            workType.update(target);
        }
    }

    private void removeTarget(EntityRef target) {
        WorkTargetComponent component = target.getComponent(WorkTargetComponent.class);
        if (component != null) {
            WorkType workType = getWorkType(component.getWork());
            workType.remove(target);
        }
    }

    private void findWork(WorkTask task) {
        WalkableBlock block;
        block = task.target.getComponent(MinionMoveComponent.class).currentBlock;
        if (block == null) {
            throw new IllegalStateException("No block " + task.target);
        }
        Vector3i currentPosition = block.getBlockPosition();
        Cluster nearestCluster = task.workType.getCluster().findNearestCluster(currentPosition);
        if (nearestCluster != null) {
            Vector3i nearestTarget = nearestCluster.findNearest(currentPosition);
            if (nearestTarget != null) {
                EntityRef work = task.workType.getWorkForTarget(nearestTarget);
                if (task.callback.workReady(nearestCluster, nearestTarget, work)) {
                    task.workType.removeRequestable(work);
                }
                return;
            }
        }
        task.callback.workReady(null, null, null);
    }

    @ReceiveEvent
    public void onAdded(OnAddedComponent event, EntityRef entityRef, WorkTargetComponent workTarget) {
        if (workTarget == null) {
            return;
        }
        Flux.just(entityRef).subscribeOn(GameScheduler.parallel()).subscribe(this::updateTarget);
    }

    @ReceiveEvent
    public void onRemove(BeforeRemoveComponent event, EntityRef entityRef, WorkTargetComponent workTarget) {
        Flux.just(entityRef).subscribeOn(GameScheduler.parallel()).subscribe(this::updateTarget);
    }

    @ReceiveEvent
    public void onChange(OnChangedComponent event, EntityRef entityRef, WorkTargetComponent workTarget) {
        Flux.just(entityRef).subscribeOn(GameScheduler.parallel()).subscribe(this::updateTarget);
    }

    @ReceiveEvent
    public void onActivated(OnActivatedComponent event, EntityRef entityRef, WorkTargetComponent workTarget) {
        if (workTarget == null) {
            return;
        }
        Flux.just(entityRef).subscribeOn(GameScheduler.parallel()).subscribe(this::updateTarget);
    }

    public void getWork(EntityRef entity, Work filter, WorkBoardCallback callback) {
        if (filter == null) {
            return;
        }
        WorkType workType = getWorkType(filter);

        Flux.just(new WorkTask(entity, workType, callback)).subscribeOn(GameScheduler.parallel()).subscribe(this::findWork);
    }

    @ReceiveEvent(components = {LocationComponent.class, CharacterComponent.class})
    public void onSelectionChanged(ApplyBlockSelectionEvent event, EntityRef entity) {
        Work work = workFactory.getWork(event.getSelectedItemEntity());
        if (work == null) {
            return;
        }
        BlockRegion selection = event.getSelection();
        for (Vector3ic pos : selection) {
            EntityRef blockEntity = blockEntityRegistry.getBlockEntityAt(pos);
            if (work.isAssignable(blockEntity)) {
                WorkTargetComponent workTargetComponent = blockEntity.getComponent(WorkTargetComponent.class);
                if (workTargetComponent != null) {
                    blockEntity.removeComponent(WorkTargetComponent.class);
                }
                workTargetComponent = new WorkTargetComponent();
                workTargetComponent.setWork(work);
                blockEntity.addComponent(workTargetComponent);
            }
        }
    }

    @Override
    public void shutdown() {
    }

    public WorkType getWorkType(Work work) {
        if (work == null) {
            return null;
        }
        WorkType workType = workTypes.get(work);
        if (workType == null) {
            workType = new WorkType(work);
            workTypes.put(work, workType);
        }
        return workType;
    }

    public interface WorkBoardCallback {
        boolean workReady(Cluster cluster, Vector3i position, EntityRef work);
    }

    private static class WorkTask {
        private EntityRef target;
        private WorkType workType;
        private WorkBoardCallback callback;

        public WorkTask(EntityRef target, WorkType type, WorkBoardCallback callback) {
            this.target = target;
            this.workType = type;
            this.callback = callback;
        }
    }
}
