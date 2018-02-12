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
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;

/**
 * Does the actual work, once the actor is in range. The child node is started.<br/>
 * <br/>
 * <b>SUCCESS</b>: when work is done (depends on work type).<br/>
 * <b>FAILURE</b>: if no work is assigned or target is not reachable.<br/>
 * <br/>
 * Auto generated javadoc - modify README.markdown instead!
 */
@BehaviorAction(name = "finish_work")
public class FinishWorkNode extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(FinishWorkNode.class);

    @Override
    public void construct(Actor actor) {
        MinionWorkComponent actorWork = actor.getComponent(MinionWorkComponent.class);
        EntityRef currentWork = actorWork.currentWork;
        if (currentWork == null) {
            return;
        }
        WorkTargetComponent jobTargetComponent = currentWork.getComponent(WorkTargetComponent.class);
        if (jobTargetComponent == null) {
            return;
        }
        Work work = jobTargetComponent.getWork();
        if (!work.canMinionWork(currentWork, actor.getEntity())) {
            logger.info("Not in range, work aborted " + currentWork);
            jobTargetComponent.assignedMinion = null;
            currentWork.saveComponent(jobTargetComponent);
            actorWork.currentWork = null;
            actor.save(actorWork);
            return;
        }
        actorWork.cooldown = work.cooldownTime();
        actor.save(actorWork);
        logger.info("Reached work " + currentWork);
        work.letMinionWork(currentWork, actor.getEntity());
    }

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        MinionWorkComponent actorWork = actor.getComponent(MinionWorkComponent.class);
        if (actorWork.currentWork != null) {
            actorWork.cooldown -= actor.getDelta();
            actor.save(actorWork);

            if (actorWork.cooldown > 0) {
                return BehaviorState.RUNNING;
            } else {
                logger.info("Work finished");
                actorWork.currentWork = null;
                actor.save(actorWork);
                return BehaviorState.SUCCESS;
            }
        }
        return BehaviorState.FAILURE;
    }

}
