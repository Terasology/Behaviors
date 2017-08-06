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

import com.google.common.collect.Sets;
import org.terasology.WorldProvidingHeadlessEnvironment;
import org.terasology.core.world.generator.AbstractBaseWorldGenerator;
import org.terasology.engine.ComponentSystemManager;
import org.terasology.engine.SimpleUri;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.internal.EngineEntityManager;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.characters.CharacterComponent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.logic.selection.ApplyBlockSelectionEvent;
import org.terasology.math.Region3i;
import org.terasology.math.geom.Vector3i;
import org.terasology.minion.move.MinionMoveComponent;
import org.terasology.minion.work.kmeans.Cluster;
import org.terasology.minion.work.systems.WalkToBlock;
import org.terasology.monitoring.PerformanceMonitor;
import org.terasology.navgraph.Entrance;
import org.terasology.navgraph.Floor;
import org.terasology.navgraph.NavGraphSystem;
import org.terasology.navgraph.WalkableBlock;
import org.terasology.pathfinding.PathfinderTestGenerator;
import org.terasology.pathfinding.componentSystem.PathfinderSystem;
import org.terasology.registry.CoreRegistry;
import org.terasology.rendering.nui.properties.OneOfProviderFactory;
import org.terasology.world.block.Block;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author synopia
 */
public class ClusterDebugger extends JFrame {
    private WorldProvidingHeadlessEnvironment env;
    private final int mapWidth;
    private final int mapHeight;
    private int level;
    private WalkableBlock hovered;
    private NavGraphSystem world;
    private EntityManager entityManager;
    private EngineEntityManager engineEntityManager;
    private WalkToBlock walkToBlock;
    private Vector3i nearest;
    private Vector3i target;
    private final WorkBoard workBoard;
    private List<Cluster> leafCluster;
    private final Object mutex = new Object();

    public ClusterDebugger() throws HeadlessException {
        env = new WorldProvidingHeadlessEnvironment();
        env.setupWorldProvider(new AbstractBaseWorldGenerator(new SimpleUri("")) {
            @Override
            public void initialize() {
                register(new PathfinderTestGenerator(true, true));
            }
        });
        env.registerBlock("Core:Dirt", new Block(), false);

        entityManager = CoreRegistry.get(EntityManager.class);
        mapWidth = 160;
        mapHeight = 100;

        WorkFactory workFactory = new WorkFactory();
        CoreRegistry.get(ComponentSystemManager.class).register(workFactory);
        CoreRegistry.put(WorkFactory.class, workFactory);

        world = new NavGraphSystem();
        CoreRegistry.get(ComponentSystemManager.class).register(world);
        CoreRegistry.put(NavGraphSystem.class, world);
        PathfinderSystem pathfinderSystem = new PathfinderSystem();
        CoreRegistry.get(ComponentSystemManager.class).register(pathfinderSystem);
        CoreRegistry.put(PathfinderSystem.class, pathfinderSystem);
        CoreRegistry.put(OneOfProviderFactory.class, new OneOfProviderFactory());

        workBoard = new WorkBoard();
        CoreRegistry.get(ComponentSystemManager.class).register(workBoard);

        for (int x = 0; x < mapWidth / 16 + 1; x++) {
            for (int z = 0; z < mapHeight / 16 + 1; z++) {
                world.updateChunk(new Vector3i(x, 0, z));
            }
        }
        level = 45;


        walkToBlock = new WalkToBlock();
        CoreRegistry.get(ComponentSystemManager.class).register(walkToBlock);

        add(new DebugPanel());
    }

    private boolean isEntrance(WalkableBlock block) {
        boolean isEntrance = false;
        for (Entrance entrance : block.floor.entrances()) {
            if (entrance.getAbstractBlock() == block) {
                isEntrance = true;
                break;
            }
        }
        return isEntrance;
    }

    public static void main(String[] args) throws InterruptedException {
        final ClusterDebugger debugger = new ClusterDebugger();
        debugger.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        debugger.pack();
        debugger.setVisible(true);

        while (true) {
            Thread.sleep(100);
            debugger.update(0.1f);
        }
    }

    public void update(float dt) {
        entityManager.getEventSystem().process();
        for (UpdateSubscriberSystem updater : CoreRegistry.get(ComponentSystemManager.class).iterateUpdateSubscribers()) {
            PerformanceMonitor.startActivity(updater.getClass().getSimpleName());
            updater.update(dt);
            PerformanceMonitor.endActivity();
        }
        WorkType workType = workBoard.getWorkType(walkToBlock);
        List<Cluster> leafs = workType.getCluster().getLeafCluster();
        synchronized (mutex) {
            leafCluster = leafs;
        }
    }

    private final class DebugPanel extends JPanel {

        private WalkableBlock block;

        private DebugPanel() {
            addMouseMotionListener(new MouseMotionListener() {
                @Override
                public void mouseDragged(MouseEvent e) {
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    int hoverX = e.getX() * mapWidth / getWidth();
                    int hoverZ = e.getY() * mapHeight / getHeight();
                    hovered = world.getBlock(new Vector3i(hoverX, level, hoverZ));
                    repaint();
                }

            });
            addMouseWheelListener(new MouseAdapter() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    level += e.getWheelRotation();
                    repaint();
                }
            });
            addMouseListener(new MouseAdapter() {


                @Override
                public void mousePressed(MouseEvent e) {
                    int clickedX = e.getX() * mapWidth / getWidth();
                    int clickedZ = e.getY() * mapHeight / getHeight();
                    block = world.getBlock(new Vector3i(clickedX, level, clickedZ));
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    int clickedX = e.getX() * mapWidth / getWidth();
                    int clickedZ = e.getY() * mapHeight / getHeight();
                    WalkableBlock lastBlock = world.getBlock(new Vector3i(clickedX, level, clickedZ));

                    if (e.getButton() == MouseEvent.BUTTON1) {
                        int minX = Math.min(block.x(), lastBlock.x());
                        int maxX = Math.max(block.x(), lastBlock.x());
                        int minY = Math.min(block.height(), lastBlock.height());
                        int maxY = Math.max(block.height(), lastBlock.height());
                        int minZ = Math.min(block.z(), lastBlock.z());
                        int maxZ = Math.max(block.z(), lastBlock.z());
                        final EntityRef item = entityManager.create();
                        WorkComponent workComponent = new WorkComponent();
                        workComponent.uri = walkToBlock.getUri();
                        item.addComponent(workComponent);
                        item.addComponent(new LocationComponent());
                        item.addComponent(new CharacterComponent());

                        final Region3i selection = Region3i.createFromMinAndSize(new Vector3i(minX, minY, minZ), new Vector3i(maxX, maxY, maxZ));
                        ApplyBlockSelectionEvent event = new ApplyBlockSelectionEvent(item, selection);
                        item.send(event);
                    } else {
                        target = new Vector3i(lastBlock.x(), lastBlock.height(), lastBlock.z());
                        EntityRef entity = entityManager.create();
                        MinionMoveComponent moveComponent = new MinionMoveComponent();
                        moveComponent.currentBlock = lastBlock;
                        entity.addComponent(moveComponent);
                        workBoard.getWork(entity, walkToBlock, (cluster, position, work) -> {
                            nearest = position;
                            return true;
                        });

                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Floor hoveredFloor = null;
            if (hovered != null) {
                hoveredFloor = hovered.floor;
            }

            for (int z = 0; z < mapHeight; z++) {
                for (int x = 0; x < mapWidth; x++) {
                    int screenX = x * getWidth() / mapWidth;
                    int screenY = z * getHeight() / mapHeight;
                    int tileWidth = (x + 1) * getWidth() / mapWidth - screenX;
                    int tileHeight = (z + 1) * getHeight() / mapHeight - screenY;
                    WalkableBlock current = world.getBlock(new Vector3i(x, level, z));
                    if (current != null) {
                        boolean isEntrance = isEntrance(current);

                        if (current.floor == hoveredFloor) {
                            if (isEntrance) {
                                g.setColor(Color.red);

                            } else {
                                g.setColor(Color.blue);
                            }
                        } else {
                            if (isEntrance) {
                                g.setColor(Color.lightGray);
                            } else {
                                g.setColor(Color.cyan);
                            }
                        }
                    } else {
                        g.setColor(Color.black);
                    }
                    g.fillRect(screenX, screenY, tileWidth, tileHeight);
                }
            }
            if (hovered != null) {
                boolean isEntrance = isEntrance(hovered);

                int screenX = hovered.x() * getWidth() / mapWidth;
                int screenY = hovered.z() * getHeight() / mapHeight;
                int tileWidth = (hovered.x() + 1) * getWidth() / mapWidth - screenX;
                int tileHeight = (hovered.z() + 1) * getHeight() / mapHeight - screenY;
                int x = screenX + tileWidth / 2;
                int y = screenY + tileHeight / 2;
                Set<Entrance> entrances;
                if (isEntrance) {
                    entrances = Sets.newHashSet();
                    for (Floor floor : hovered.floor.neighborRegions) {
                        entrances.addAll(floor.entrances());
                    }
                } else {
                    entrances = Sets.newHashSet(hovered.floor.entrances());
                }

                for (Entrance entrance : entrances) {
                    WalkableBlock current = entrance.getAbstractBlock();
                    screenX = current.x() * getWidth() / mapWidth;
                    screenY = current.z() * getHeight() / mapHeight;
                    tileWidth = (current.x() + 1) * getWidth() / mapWidth - screenX;
                    tileHeight = (current.z() + 1) * getHeight() / mapHeight - screenY;
                    int ex = screenX + tileWidth / 2;
                    int ey = screenY + tileHeight / 2;
                    if (current.height() == level) {
                        g.setColor(Color.BLACK);
                    } else {
                        g.setColor(Color.LIGHT_GRAY);
                    }
                    g.drawLine(x, y, ex, ey);
                }
            }

            synchronized (mutex) {
                if (leafCluster != null) {
                    int id = 1;
                    for (Cluster cluster : leafCluster) {
                        drawCluster(g, cluster, (float) id / leafCluster.size());
                        id++;
                    }
                }
            }
            if (nearest != null) {
                drawBlock(g, nearest.x, nearest.z, "O", Color.white);
            }
            if (target != null) {
                drawBlock(g, target.x, target.z, "X", Color.white);
            }
        }

        private void drawCluster(Graphics g, Cluster parent, float color) {
            Map<Vector3i, Cluster.Distance> distances = parent.getDistances();
            Color col = new Color(color, color, color);
            for (Map.Entry<Vector3i, Cluster.Distance> entry : distances.entrySet()) {
                Vector3i position = entry.getKey();
                drawBlock(g, position.x, position.z, "", col);
            }
        }

        private void drawBlock(Graphics g, int x, int z, String text, Color color) {
            int screenX = x * getWidth() / mapWidth;
            int screenY = z * getHeight() / mapHeight;
            int tileWidth = (x + 1) * getWidth() / mapWidth - screenX;
            int tileHeight = (z + 1) * getHeight() / mapHeight - screenY;
            g.setColor(color);
            g.fillRect(screenX, screenY, tileWidth, tileHeight);
            g.setColor(Color.black);
            g.drawString(text, screenX, screenY + 8);
        }
    }
}
