// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.behaviors.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.gestalt.module.sandbox.API;
import org.terasology.nui.properties.TextField;

/**
 * Logs a message into the console when called and returns SUCCESS
 */
@API
@BehaviorAction(name = "log")
public class LogAction extends BaseAction {
    public static final Logger logger = LoggerFactory.getLogger(LogAction.class
    );

    @TextField
    public String message;

    @Override
    public void construct(Actor actor) {
        actor.setValue(getId(), message);
    }

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        logger.debug(String.format("Actor %s logs message: %s ", actor.getEntity().toString(), actor.getValue(getId())));
        return BehaviorState.SUCCESS;
    }
}
