// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion.work;

import com.google.common.collect.Maps;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.BeforeRemoveComponent;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnAddedComponent;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnChangedComponent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.engine.logic.characters.CharacterComponent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.logic.selection.ApplyBlockSelectionEvent;
import org.terasology.engine.registry.In;
import org.terasology.engine.registry.Share;
import org.terasology.engine.utilities.concurrency.Task;
import org.terasology.engine.utilities.concurrency.TaskMaster;
import org.terasology.engine.world.BlockEntityRegistry;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.minion.move.MinionMoveComponent;
import org.terasology.minion.work.kmeans.Cluster;
import org.terasology.navgraph.NavGraphChanged;
import org.terasology.navgraph.WalkableBlock;

import java.util.Map;

/**
 *
 */
@RegisterSystem
@Share(value = WorkBoard.class)
public class WorkBoard extends BaseComponentSystem implements UpdateSubscriberSystem {
    private static final Logger logger = LoggerFactory.getLogger(WorkBoard.class);
    private final Map<Work, WorkType> workTypes = Maps.newHashMap();
    private TaskMaster<WorkBoardTask> taskMaster = TaskMaster.createPriorityTaskMaster("Workboard", 1, 1024);

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
        offer(new UpdateChunkTask());
    }

    @ReceiveEvent
    public void onAdded(OnAddedComponent event, EntityRef entityRef, WorkTargetComponent workTarget) {
        if (workTarget == null) {
            return;
        }
        offer(new UpdateTargetTask(entityRef));
    }

    @ReceiveEvent
    public void onActivated(OnActivatedComponent event, EntityRef entityRef, WorkTargetComponent workTarget) {
        if (workTarget == null) {
            return;
        }
        offer(new UpdateTargetTask(entityRef));
    }

    @ReceiveEvent
    public void onRemove(BeforeRemoveComponent event, EntityRef entityRef, WorkTargetComponent workTarget) {
        offer(new RemoveTargetTask(entityRef));
    }

    @ReceiveEvent
    public void onChange(OnChangedComponent event, EntityRef entityRef, WorkTargetComponent workTarget) {
        offer(new UpdateTargetTask(entityRef));
    }

    public void getWork(EntityRef entity, Work filter, WorkBoardCallback callback) {
        if (filter == null) {
            return;
        }
        WorkType workType = getWorkType(filter);
        offer(new FindWorkTask(entity, workType, callback));
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
        taskMaster.shutdown(new ShutdownTask(), false);
    }

    public void offer(WorkBoardTask task) {
        taskMaster.offer(task);
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

    public interface WorkBoardTask extends Task, Comparable<WorkBoardTask> {
        int getPriority();
    }

    private final class UpdateChunkTask implements WorkBoardTask {
        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public int compareTo(WorkBoardTask o) {
            return Integer.compare(this.getPriority(), o.getPriority());
        }

        @Override
        public String getName() {
            return "WorkBoard:UpdateChunk";
        }

        @Override
        public void run() {
            for (EntityRef work : entityManager.getEntitiesWith(WorkTargetComponent.class)) {
                WorkTargetComponent workTargetComponent = work.getComponent(WorkTargetComponent.class);
                WorkType workType = getWorkType(workTargetComponent.getWork());
                if (workType != null) {
                    workType.update(work);
                }
            }
        }

        @Override
        public boolean isTerminateSignal() {
            return false;
        }
    }

    private final class UpdateTargetTask implements WorkBoardTask {
        private EntityRef target;

        private UpdateTargetTask(EntityRef target) {
            this.target = target;
        }

        @Override
        public int getPriority() {
            return 1;
        }

        @Override
        public int compareTo(WorkBoardTask o) {
            return Integer.compare(this.getPriority(), o.getPriority());
        }

        @Override
        public String getName() {
            return "WorkBoard:UpdateTarget";
        }

        @Override
        public void run() {
            WorkTargetComponent component = target.getComponent(WorkTargetComponent.class);
            if (component != null) {
                WorkType workType = getWorkType(component.getWork());
                workType.update(target);
            }
        }

        @Override
        public boolean isTerminateSignal() {
            return false;
        }
    }

    private final class RemoveTargetTask implements WorkBoardTask {
        private EntityRef target;

        private RemoveTargetTask(EntityRef target) {
            this.target = target;
        }

        @Override
        public int getPriority() {
            return 2;
        }

        @Override
        public int compareTo(WorkBoardTask o) {
            return Integer.compare(this.getPriority(), o.getPriority());
        }

        @Override
        public String getName() {
            return "WorkBoard:RemoveTarget";
        }

        @Override
        public void run() {
            WorkTargetComponent component = target.getComponent(WorkTargetComponent.class);
            if (component != null) {
                WorkType workType = getWorkType(component.getWork());
                workType.remove(target);
            }
        }

        @Override
        public boolean isTerminateSignal() {
            return false;
        }
    }

    private static final class FindWorkTask implements WorkBoardTask {
        private EntityRef target;
        private WorkType workType;
        private WorkBoardCallback callback;

        private FindWorkTask(EntityRef target, WorkType workType, WorkBoardCallback callback) {
            this.target = target;
            this.workType = workType;
            this.callback = callback;
        }

        @Override
        public int getPriority() {
            return 10;
        }

        @Override
        public int compareTo(WorkBoardTask o) {
            return Integer.compare(this.getPriority(), o.getPriority());
        }

        @Override
        public String getName() {
            return "WorkBoard:FindWorkTask";
        }

        @Override
        public void run() {
            WalkableBlock block;
            block = target.getComponent(MinionMoveComponent.class).currentBlock;
            if (block == null) {
                throw new IllegalStateException("No block " + target);
            }
            Vector3i currentPosition = block.getBlockPosition();
            Cluster nearestCluster = workType.getCluster().findNearestCluster(currentPosition);
            if (nearestCluster != null) {
                Vector3i nearestTarget = nearestCluster.findNearest(currentPosition);
                if (nearestTarget != null) {
                    EntityRef work = workType.getWorkForTarget(nearestTarget);
                    if (callback.workReady(nearestCluster, nearestTarget, work)) {
                        workType.removeRequestable(work);
                    }
                    return;
                }
            }
            callback.workReady(null, null, null);
        }

        @Override
        public boolean isTerminateSignal() {
            return false;
        }
    }

    public static class ShutdownTask implements WorkBoardTask {
        @Override
        public int getPriority() {
            return -1;
        }

        @Override
        public int compareTo(WorkBoardTask o) {
            return Integer.compare(this.getPriority(), o.getPriority());
        }

        @Override
        public String getName() {
            return "WorkBoard:ShutdownTask";
        }

        @Override
        public void run() {

        }

        @Override
        public boolean isTerminateSignal() {
            return true;
        }
    }
}
