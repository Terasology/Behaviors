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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.registry.In;
import org.terasology.rendering.nui.properties.OneOf;

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
    @OneOf.Provider(name = "work")
    private String filter;

    private static final Logger logger = LoggerFactory.getLogger(FindWorkNode.class);

    @In
    private WorkBoard workBoard;
    @In
    private WorkFactory workFactory;


    @Override
    public void construct(final Actor actor) {
        final MinionWorkComponent actorWork = actor.getComponent(MinionWorkComponent.class);
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
            workBoard.getWork(actor.getEntity(), actorWork.filter, (WorkBoard.WorkBoardCallback) (cluster, position, work) -> {
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
