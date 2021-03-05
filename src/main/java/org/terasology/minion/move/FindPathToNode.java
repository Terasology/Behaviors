// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion.move;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import org.joml.Vector3f;
import org.terasology.logic.behavior.BehaviorAction;
import org.terasology.logic.behavior.core.Actor;
import org.terasology.logic.behavior.core.BaseAction;
import org.terasology.logic.behavior.core.BehaviorState;
import org.terasology.navgraph.NavGraphSystem;
import org.terasology.navgraph.WalkableBlock;
import org.terasology.pathfinding.componentSystem.PathfinderSystem;
import org.terasology.pathfinding.model.Path;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.In;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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

    private final Executor executor = Executors.newSingleThreadExecutor((runnable)-> {
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        return thread;
    });

    @Override
    public void setup() {
        navGraphSystem = CoreRegistry.get(NavGraphSystem.class);
        pathfinderSystem = CoreRegistry.get(PathfinderSystem.class);
    }

    @Override
    public void construct(final Actor actor) {
        if (pathfinderSystem == null) {
            setup();
        }
        final MinionMoveComponent moveComponent = actor.getComponent(MinionMoveComponent.class);
        Vector3f targetLocation = moveComponent.target;
        moveComponent.path = null;
        actor.save(moveComponent);
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
        }, executor);
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

