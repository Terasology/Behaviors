/*
 * Copyright 2015 MovingBlocks
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
package org.terasology.minion.move;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.math.geom.Vector3f;
import org.terasology.navgraph.NavGraphSystem;
import org.terasology.navgraph.WalkableBlock;
import org.terasology.pathfinding.componentSystem.PathfinderSystem;
import org.terasology.pathfinding.model.Path;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.In;

import java.util.Arrays;
import java.util.List;

/**
 * Requests a path to a target defined using the <b>MinionMoveComponent.target</b>.<br/>
 * <br/>
 * <b>SUCCESS</b> / <b>FAILURE</b>: when paths is found or not found (invalid).<br/>
 * <b>RUNNING</b>: as long as path is searched.<br/>
 * <br/>
 * Auto generated javadoc - modify README.markdown instead!
 */
@BehaviorAction(name = "find_path")
public class FindPathToNode extends BaseAction {

    @In
    private transient NavGraphSystem navGraphSystem;

    @In
    private transient PathfinderSystem pathfinderSystem;

    @Override
    public void setup() {
        navGraphSystem = CoreRegistry.get(NavGraphSystem.class);
        pathfinderSystem = CoreRegistry.get(PathfinderSystem.class);
    }

    @Override
    public void construct(final Actor actor) {
        if(pathfinderSystem==null){setup();}
        final MinionMoveComponent moveComponent = actor.getComponent(MinionMoveComponent.class);
        Vector3f targetLocation = moveComponent.target;
        WalkableBlock currentBlock = moveComponent.currentBlock;
        if (currentBlock == null || targetLocation == null) {
            moveComponent.path = Path.INVALID;
            return;
        }
        WalkableBlock workTarget = navGraphSystem.getBlock(targetLocation);
        if (workTarget == null) {
            moveComponent.path = Path.INVALID;
            return;
        }
        SettableFuture<List<Path>> pathFuture = pathfinderSystem.requestPath(
                actor.getEntity(), currentBlock.getBlockPosition(),
                Arrays.asList(workTarget.getBlockPosition()));

        Futures.addCallback(pathFuture, new FutureCallback<List<Path>>() {
                    @Override
                    public void onSuccess(List<Path> paths) {
                        if (paths == null) {
                            moveComponent.path = Path.INVALID;
                        } else if (paths.size() > 0) {
                            moveComponent.path = paths.get(0);
                        }
                        actor.save(moveComponent);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        moveComponent.path = Path.INVALID;
                    }
        });
    }

    @Override
    public BehaviorState modify(Actor actor, BehaviorState result) {
        final MinionMoveComponent moveComponent = actor.getComponent(MinionMoveComponent.class);
        if (moveComponent.path == null) {
            return BehaviorState.RUNNING;
        }
        return moveComponent.path == Path.INVALID ? BehaviorState.FAILURE : BehaviorState.SUCCESS;
    }
}

