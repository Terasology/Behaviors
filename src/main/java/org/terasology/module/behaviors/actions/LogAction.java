// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.context.annotation.API;
import org.terasology.nui.properties.TextField;

/**
 * Logs a debug message when called. Always returns SUCCESS.
 * <p>
 * For example, you can add such a log action at the beginning of a sequence and observe whether/when it is printend.
 * <pre>
 *     { log: { message: "starting 'myBehavior' sequence"} }
 * </pre>
 * You may need to adjust the logging configuration to make logs on 'DEBUG' level from the {@link LogAction} class visible.
 */
@API
@BehaviorAction(name = "log")
public class LogAction extends BaseAction {
    public static final Logger logger = LoggerFactory.getLogger(LogAction.class);

    @TextField
    public String message;

    @Override
    public void construct(Actor actor) {
        actor.setValue(getId(), message);
    }

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        logger.debug("Actor {}: {}", actor.getEntity().getId(), actor.getValue(getId()));
        return BehaviorState.SUCCESS;
    }
}
