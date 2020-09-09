// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion.move;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.logic.behavior.BehaviorAction;
import org.terasology.engine.logic.behavior.core.Actor;
import org.terasology.engine.logic.behavior.core.BaseAction;
import org.terasology.engine.logic.behavior.core.BehaviorState;
import org.terasology.engine.logic.characters.CharacterMoveInputEvent;
import org.terasology.engine.logic.characters.CharacterMovementComponent;
import org.terasology.math.geom.Vector3f;

/**
 * Trigger a single jump into the air.<br/> <br/>
 * <b>SUCCESS</b>: when the actor is grounded after the jump again.<br/>
 * <br/> Auto generated javadoc - modify README.markdown instead!
 */
@BehaviorAction(name = "jump")
public class JumpNode extends BaseAction {

    @Override
    public void construct(Actor actor) {


        long delta = (long) (actor.getDelta() * 1000);
        Event event = new CharacterMoveInputEvent(0, 0, 0, new Vector3f(), false, false, true, delta);
        actor.getEntity().send(event);

    }

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {

        return actor.getComponent(CharacterMovementComponent.class).grounded ? BehaviorState.SUCCESS :
                BehaviorState.RUNNING;
    }

}
