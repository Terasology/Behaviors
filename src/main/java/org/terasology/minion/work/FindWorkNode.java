// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion.work;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.nui.properties.OneOf;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.In;

/**
 * <b>Properties</b>: <b>filter</b><br/>
 * <br/>
 * Searches for open work of specific type (<b>filter</b>). If work is found, the actor is assigned.<br/>
 * <br/>
 * <b>SUCCESS</b>: When work is found and assigned.<br/>
 * <br/>
 * Auto generated javadoc - modify README.markdown instead!
 */
@BehaviorAction(name = "find_work")
public class FindWorkNode extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(FindWorkNode.class);

    @OneOf.Provider(name = "work")
    private String filter = "Behaviors:removeBlock";

    @In
    private transient WorkBoard workBoard;

    @In
    private transient WorkFactory workFactory;

    @Override
    public void setup() {
        workBoard = CoreRegistry.get(WorkBoard.class);
        workFactory = CoreRegistry.get(WorkFactory.class);
    }

    @Override
    public void construct(final Actor actor) {


        final MinionWorkComponent actorWork = actor.getComponent(MinionWorkComponent.class);
//        logger.info("FINDWORK CONSTRUCT");
        if (actorWork.currentWork != null) {
            WorkTargetComponent currentJob = actorWork.currentWork.getComponent(WorkTargetComponent.class);
            if (currentJob != null) {
                logger.info("Removing current work from actor " + currentJob.getUri() + " at " + actorWork.currentWork);
                currentJob.assignedMinion = null;

                actorWork.currentWork.saveComponent(currentJob);
            }
        }
        actorWork.filter = filter != null ? workFactory.getWork(filter) : null;
        if (actorWork.filter != null) {
            workBoard.getWork(actor.getEntity(), actorWork.filter, (cluster, position, work) -> {
                actorWork.workSearchDone = true;
                actorWork.currentWork = work;
                actorWork.target = position;
                actor.save(actorWork);
                return true;
            });
        } else {
            actorWork.workSearchDone = true;
        }
        actor.save(actorWork);
    }

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        if (workFactory == null) {
            setup();
        }


        if (!constructed) {

            construct(actor);
            constructed = true;
        }
        final MinionWorkComponent actorWork = actor.getComponent(MinionWorkComponent.class);
        if (!actorWork.workSearchDone) {
            return BehaviorState.RUNNING;
        }
        if (actorWork.currentWork != null) {
            WorkTargetComponent workTargetComponent = actorWork.currentWork.getComponent(WorkTargetComponent.class);
            if (workTargetComponent != null && workTargetComponent.getWork() != null) {
                logger.info("Found new work for " + toString() + " " + workTargetComponent.getUri() + " at " + actorWork.currentWork);
                workTargetComponent.assignedMinion = actor.getEntity();
                actorWork.currentWork.saveComponent(workTargetComponent);
                return BehaviorState.SUCCESS;
            }
        }
        return BehaviorState.FAILURE;
    }
}
