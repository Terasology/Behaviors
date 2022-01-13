// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.flexiblemovement;

import com.google.common.collect.Sets;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.characters.CharacterMovementComponent;
import org.terasology.engine.logic.characters.CharacterTeleportEvent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.physics.engine.PhysicsEngine;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.WorldProvider;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.engine.world.block.BlockRegionc;
import org.terasology.engine.world.block.Blocks;
import org.terasology.engine.world.chunks.ChunkProvider;
import org.terasology.moduletestingenvironment.MTEExtension;
import org.terasology.moduletestingenvironment.ModuleTestingHelper;
import org.terasology.moduletestingenvironment.extension.Dependencies;

import java.util.stream.Stream;

@Dependencies("FlexibleMovement")
@Tag("MteTest")
@ExtendWith(MTEExtension.class)
public class MovementTests {
    private static final Logger logger = LoggerFactory.getLogger(MovementTests.class);
    @In
    protected ModuleTestingHelper helper;
    @In
    protected WorldProvider worldProvider;
    @In
    protected BlockManager blockManager;
    @In
    protected EntityManager entityManager;
    @In
    protected PhysicsEngine physicsEngine;
    @In
    private ChunkProvider chunkProvider;

    public static Stream<Arguments> parameters() {
        return Stream.of(
                Arguments.of(
                        "simple wall",
                        new String[]{
                                "XXX|XXX|XXX",
                                "   |   |XXX",
                                "XXX|XXX|XXX",
                        }, new String[]{
                                "?  |   |   ",
                                "   |   |   ",
                                "  !|   |   "
                        },
                        0.9f,
                        0.3f,
                        new String[]{"flying"}
                ),
                Arguments.of(
                        "large leaping - steps",
                        new String[]{
                                "XXX XXX|XXX XXX|XXX XXX",
                                "XXXXXXX|XXXXXXX|XXXXXXX",
                                "XXX XXX|XXX XXX|XXX XXX",
                                "XXXXXXX|XXXXXXX|XXXXXXX",
                                "XXXXXXX|XXXXXXX|XXXXXXX",
                                "XXXXXXX|XXXXXXX|XXXXXXX"
                        }, new String[]{
                                "       |       |       ",
                                "       | ?   ! |       ",
                                "       |       |       ",
                                "       |       |       ",
                                "       |       |       ",
                                "       |       |       "
                        },
                        2.7f,
                        1.2f,
                        new String[]{"walking", "leaping"}
                ),
                Arguments.of(
                        "leap",
                        new String[]{
                                "~  |~~~|~~~",
                                "~  |~~~|~~~",
                                "~~~|~~~|~~~",
                        }, new String[]{
                                "?  |123|~~~",
                                "   |  !|~~~",
                                "   |   |~~~"
                        },
                        0.9f,
                        0.3f,
                        new String[]{"swimming"}
                ),
                Arguments.of(
                        "diagonal",
                        new String[]{
                                "~  |~  ",
                                "~  |~  ",
                                "~~~|~~~"
                        }, new String[]{
                                "?  |   ",
                                "1  |   ",
                                "23!|   "
                        },
                        0.9f,
                        0.3f,
                        new String[]{"swimming"}
                ),
                Arguments.of(
                        "straight",
                        new String[]{
                                "~  ",
                                "~  ",
                                "~~~",
                        }, new String[]{
                                "?  ",
                                "1  ",
                                "!  "
                        },
                        0.9f,
                        0.3f,
                        new String[]{"swimming"}
                ),
                Arguments.of(
                        "corridor",
                        new String[]{
                                "~~~~~~~~~~~~~~~",
                                "~            ~~",
                                "~ ~~~~~~~~~~~~~",
                                "~~~            ",
                                "               ",
                        }, new String[]{
                                "?123456789abcd ",
                                "             e ",
                                "  qponmlkjihgf ",
                                "  !            ",
                                "               ",
                        },
                        0.9f,
                        0.3f,
                        new String[]{"swimming"}
                ),
                Arguments.of(
                        "three dimensional moves",
                        new String[]{
                                "~~~|~~ |   ",
                                "~ ~|~~~| ~~",
                                "~~~| ~ | ~~"
                        }, new String[]{
                                "?  |   |   ",
                                "   | 1 |   ",
                                "   |   |  !"
                        },
                        0.9f,
                        0.3f,
                        new String[]{"swimming"}
                ),
                Arguments.of(
                        "leap",
                        new String[]{
                                "~  |   ",
                                "~  |XXX"
                        }, new String[]{
                                "?  |   ",
                                "1  |23!"
                        },
                        0.9f,
                        0.3f,
                        new String[]{"walking", "leaping", "swimming"}
                ),
                Arguments.of(
                        "three dimensional moves",
                        new String[]{
                                "~~~|~~ |   ",
                                "~ ~|~~~| XX",
                                "~~~| ~ | XX"
                        }, new String[]{
                                "?  |   |   ",
                                "   | 1 |   ",
                                "   |   |  !"
                        },
                        0.9f,
                        0.3f,
                        new String[]{"walking", "leaping", "swimming"}
                ),
                Arguments.of(
                        "straight",
                        new String[]{
                                "X  ",
                                "X  ",
                                "XXX",
                        }, new String[]{
                                "?  ",
                                "1  ",
                                "!  "
                        },
                        0.9f,
                        0.3f,
                        new String[]{"walking", "leaping"}
                ),
                Arguments.of(
                        "leap",
                        new String[]{
                                "X  |XXX|XXX|XXX",
                                "X  |XXX|XXX|XXX",
                        }, new String[]{
                                "?  |123|XXX|XXX",
                                "   |  !|XXX|XXX",
                        },
                        0.9f,
                        0.3f,
                        new String[]{"walking", "leaping"}
                ), Arguments.of(
                        "diagonal",
                        new String[]{
                                "X  |X  ",
                                "X  |X  ",
                                "XXX|XXX"
                        }, new String[]{
                                "?  |   ",
                                "1  |   ",
                                "23!|   "
                        },
                        0.9f,
                        0.3f,
                        new String[]{"walking", "leaping"}
                ), Arguments.of(
                        "corridor",
                        new String[]{
                                "XXXXXXXXXXXXXXX",
                                "X            XX",
                                "X XXXXXXXXXXXXX",
                                "XXX            ",
                                "               ",
                        }, new String[]{
                                "?123456789abcd ",
                                "             e ",
                                "  qponmlkjihgf ",
                                "  !            ",
                                "               ",
                        },
                        0.9f,
                        0.3f,
                        new String[]{"walking", "leaping"}
                ),
                Arguments.of(
                        "three dimensional moves",
                        new String[]{
                                "XXX|XX |   ",
                                "X X|XXX| XX",
                                "XXX| X | XX"
                        }, new String[]{
                                "?  |   |   ",
                                "   | 1 |   ",
                                "   |   |  !"
                        },
                        0.9f,
                        0.3f,
                        new String[]{"walking", "leaping"}
                ),
                Arguments.of(
                        "jump over",
                        new String[]{
                                "X X|XXX|XXX|XXX"
                        }, new String[]{
                                "? !|123|   |   "
                        },
                        0.9f,
                        0.3f,
                        new String[]{"walking", "leaping"}
                )

        );
    }

    @MethodSource("parameters")
    @ParameterizedTest(name = "{5}: {0}")
    @DisplayName("Check Movement plugins")
    void test(String name, String[] world, String[] path, float charHeight, float charRadius, String... movementTypes) {
        int airHeight = 41;

        setupWorld(world, airHeight);

        // find start and goal positions from path data
        Vector3i start = new Vector3i();
        Vector3i stop = new Vector3i();
        detectPath(path, airHeight, start, stop);

        EntityRef entity = createMovingCharacter(charHeight, charRadius, start, stop, movementTypes);

        helper.runUntil(() -> Blocks.toBlockPos(entity.getComponent(LocationComponent.class)
                .getWorldPosition(new Vector3f())).distance(start) <= 0.5F);

        boolean result = helper.runWhile(() -> {
            Vector3f pos = entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f());
            logger.warn("pos: {}", pos);
            return Blocks.toBlockPos(pos).distance(stop) > 0;
        });
        Assertions.assertFalse(result, () -> String.format("Test character (at %s) cannot reach destination point (at %s)",
                Blocks.toBlockPos(entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f())),
                stop
                ));
    }

    @AfterEach
    void cleanUp() {
        chunkProvider.purgeWorld();
    }

    private EntityRef createMovingCharacter(float height, float radius, Vector3i start, Vector3i stop, String... movementTypes) {
        EntityRef entity = entityManager.create("flexiblemovement:testcharacter");
        entity.send(new CharacterTeleportEvent(new Vector3f(start)));

        FlexibleMovementComponent flexibleMovementComponent = entity.getComponent(FlexibleMovementComponent.class);
        flexibleMovementComponent.setPathGoal(stop);
        flexibleMovementComponent.movementTypes.clear();
        flexibleMovementComponent.movementTypes.addAll(Sets.newHashSet(movementTypes));
        entity.saveComponent(flexibleMovementComponent);

        CharacterMovementComponent charMovementComponent = entity.getComponent(CharacterMovementComponent.class);
        charMovementComponent.height = height;
        charMovementComponent.radius = radius;
        entity.saveComponent(charMovementComponent);

        physicsEngine.removeCharacterCollider(entity);
        physicsEngine.getCharacterCollider(entity);
        return entity;
    }

    /**
     * Detect path for entity at map {@code path}
     * @param path map with path
     * @param airHeight air height for world
     * @param start (!) ref parameter - set start point
     * @param stop  (!) ref parameter - set end point
     */
    private void detectPath(String[] path, int airHeight, Vector3i start, Vector3i stop) {
        for (int z = 0; z < path.length; z++) {
            int y = airHeight;
            String row = path[z];
            int x = 0;
            for (char c : row.toCharArray()) {
                switch (c) {
                    case '?':
                        start.set(x, y, z);
                        x += 1;
                        break;
                    case '!':
                        stop.set(x, y, z);
                        x += 1;
                        break;
                    case '|':
                        y += 1;
                        x = 0;
                        break;
                    default:
                        x += 1;
                        break;
                }
            }
        }
    }

    private void setupWorld(String[] world, int airHeight) {
        Block air = blockManager.getBlock("engine:air");
        Block dirt = blockManager.getBlock("CoreAssets:dirt");
        Block water = blockManager.getBlock("CoreAssets:water");

        BlockRegionc extents = getPaddedExtents(world, airHeight);

        for (Vector3ic pos : extents) {
            helper.forceAndWaitForGeneration(pos);
        }

        for (Vector3ic pos : extents) {
            worldProvider.setBlock(pos, dirt);
        }

        // set blocks from world data
        for (int z = 0; z < world.length; z++) {
            int y = airHeight;
            String row = world[z];
            int x = 0;
            for (char c : row.toCharArray()) {
                switch (c) {
                    case 'X':
                        worldProvider.setBlock(new Vector3i(x, y, z), air);
                        x += 1;
                        break;
                    case ' ':
                        worldProvider.setBlock(new Vector3i(x, y, z), dirt);
                        x += 1;
                        break;
                    case '~':
                        worldProvider.setBlock(new Vector3i(x, y, z), water);
                        x += 1;
                        break;
                    case '|':
                        y += 1;
                        x = 0;
                        break;
                    default:
                        x += 1;
                        break;
                }
            }
        }
    }

    private BlockRegionc getPaddedExtents(String[] world, int airHeight) {
        Vector3i minPoint = new Vector3i(0, airHeight, 0);
        Vector3i maxPoint = new Vector3i();

        for (int z = 0; z < world.length; z++) {
            int y = airHeight;
            String row = world[z];
            int x = 0;
            for (char c : row.toCharArray()) {
                maxPoint.set(x, y, z);
                switch (c) {
                    case 'X':
                        x += 1;
                        break;
                    case ' ':
                        x += 1;
                        break;
                    case '|':
                        y += 1;
                        x = 0;
                        break;
                    default:
                        x += 1;
                        break;
                }
            }
        }
        BlockRegion extents = new BlockRegion(minPoint, maxPoint);
        extents.expand(1, 1, 1);
        return extents;
    }
}
