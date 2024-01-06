// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityBuilder;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.integrationenvironment.ModuleTestingHelper;
import org.terasology.engine.logic.characters.CharacterMovementComponent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.physics.engine.PhysicsEngine;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.WorldProvider;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.engine.world.block.BlockRegionc;
import org.terasology.engine.world.block.Blocks;
import org.terasology.module.behaviors.components.MinionMoveComponent;
import org.terasology.engine.integrationenvironment.jupiter.IntegrationEnvironment;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@IntegrationEnvironment(dependencies = "Behaviors")
public class MovementTests {
    private static final Logger logger = LoggerFactory.getLogger(MovementTests.class);

    private static final long TIMEOUT = 10_000;
    private static final int AIR_HEIGHT = 41;
    private static final float CHAR_HEIGHT = 0.9f;
    private static final float CHAR_RADIUS = 0.3f;
    private static final String[] DEFAULT_MOVEMENT_MODES = {"walking", "leaping", "falling"};

    private static final String[] THREE_BY_THREE_CROSS_FLAT_WORLD = {
            "X X",
            "   ",
            "X X"
    };

    private static final String[] THREE_BY_THREE_OPEN_FLAT_WORLD = {
            "   ",
            "   ",
            "   "
    };

    private static final String[] THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD = {
            "XXX|X X",
            "X X|   ",
            "XXX|X X"
    };

    private static final String[] THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD = {
            "X X|   ",
            "   |   ",
            "X X|   "
    };

    private static final String[] THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD = {
            "XXX|   ",
            "X X|   ",
            "XXX|   "
    };

    private static final String[] SINGLE_FLAT_STEP_NORTH_PATH = {
            " ! ",
            " ? ",
            "   "
    };

    private static final String[] SINGLE_FLAT_STEP_SOUTH_PATH = {
            "   ",
            " ? ",
            " ! "
    };

    private static final String[] SINGLE_FLAT_STEP_WEST_PATH = {
            "   ",
            "!? ",
            "   "
    };

    private static final String[] SINGLE_FLAT_STEP_EAST_PATH = {
            "   ",
            " ?!",
            "   "
    };

    private static final String[] DIAGONAL_FLAT_STEP_NORTH_WEST_PATH = {
            "!  ",
            " ? ",
            "   "
    };

    private static final String[] DIAGONAL_FLAT_STEP_NORTH_EAST_PATH = {
            "  !",
            " ? ",
            "   "
    };

    private static final String[] DIAGONAL_FLAT_STEP_SOUTH_WEST_PATH = {
            "   ",
            " ? ",
            "!  "
    };

    private static final String[] DIAGONAL_FLAT_STEP_SOUTH_EAST_PATH = {
            "   ",
            " ? ",
            "  !"
    };

    private static final String[] SINGLE_ASCENDING_STEP_NORTH_PATH = {
            "   | ! ",
            " ? |   ",
            "   |   "
    };

    private static final String[] SINGLE_ASCENDING_STEP_SOUTH_PATH = {
            "   |   ",
            " ? |   ",
            "   | ! "
    };

    private static final String[] SINGLE_ASCENDING_STEP_WEST_PATH = {
            "   |   ",
            " ? |!  ",
            "   |   "
    };

    private static final String[] SINGLE_ASCENDING_STEP_EAST_PATH = {
            "   |   ",
            " ? |  !",
            "   |   "
    };

    private static final String[] DIAGONAL_ASCENDING_STEP_NORTH_WEST_PATH = {
            "   |!  ",
            " ? |   ",
            "   |   "
    };

    private static final String[] DIAGONAL_ASCENDING_STEP_NORTH_EAST_PATH = {
            "   |  !",
            " ? |   ",
            "   |   "
    };

    private static final String[] DIAGONAL_ASCENDING_STEP_SOUTH_WEST_PATH = {
            "   |   ",
            " ? |   ",
            "   |!  "
    };

    private static final String[] DIAGONAL_ASCENDING_STEP_SOUTH_EAST_PATH = {
            "   |   ",
            " ? |   ",
            "   |  !"
    };

    private static final String[] SINGLE_DESCENDING_STEP_NORTH_PATH = {
            "   |   ",
            " ! |   ",
            "   | ? "
    };

    private static final String[] SINGLE_DESCENDING_STEP_SOUTH_PATH = {
            "   | ? ",
            " ! |   ",
            "   |   "
    };

    private static final String[] SINGLE_DESCENDING_STEP_WEST_PATH = {
            "   |   ",
            " ! |  ?",
            "   |   "
    };

    private static final String[] SINGLE_DESCENDING_STEP_EAST_PATH = {
            "   |   ",
            " ! |?  ",
            "   |   "
    };

    private static final String[] DIAGONAL_DESCENDING_STEP_NORTH_WEST_PATH = {
            "   |   ",
            " ! |   ",
            "   |  ?"
    };

    private static final String[] DIAGONAL_DESCENDING_STEP_NORTH_EAST_PATH = {
            "   |   ",
            " ! |   ",
            "   |?  "
    };

    private static final String[] DIAGONAL_DESCENDING_STEP_SOUTH_WEST_PATH = {
            "   |  ?",
            " ! |   ",
            "   |   "
    };

    private static final String[] DIAGONAL_DESCENDING_STEP_SOUTH_EAST_PATH = {
            "   |?  ",
            " ! |   ",
            "   |   "
    };

    private static EntityRef entity = EntityRef.NULL;

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

    public static Stream<Arguments> walkingMovementParameters() {
        return Stream.of(
                Arguments.of("succeed flat north", THREE_BY_THREE_CROSS_FLAT_WORLD, SINGLE_FLAT_STEP_NORTH_PATH, true),
                Arguments.of("succeed flat south", THREE_BY_THREE_CROSS_FLAT_WORLD, SINGLE_FLAT_STEP_SOUTH_PATH, true),
                Arguments.of("succeed flat west", THREE_BY_THREE_CROSS_FLAT_WORLD, SINGLE_FLAT_STEP_WEST_PATH, true),
                Arguments.of("succeed flat east", THREE_BY_THREE_CROSS_FLAT_WORLD, SINGLE_FLAT_STEP_EAST_PATH, true),
                Arguments.of("succeed flat northwest", THREE_BY_THREE_OPEN_FLAT_WORLD, DIAGONAL_FLAT_STEP_NORTH_WEST_PATH, true),
                Arguments.of("succeed flat northeast", THREE_BY_THREE_OPEN_FLAT_WORLD, DIAGONAL_FLAT_STEP_NORTH_EAST_PATH, true),
                Arguments.of("succeed flat southwest", THREE_BY_THREE_OPEN_FLAT_WORLD, DIAGONAL_FLAT_STEP_SOUTH_WEST_PATH, true),
                Arguments.of("succeed flat southeast", THREE_BY_THREE_OPEN_FLAT_WORLD, DIAGONAL_FLAT_STEP_SOUTH_EAST_PATH, true),
                Arguments.of("fail ascend north", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_ASCENDING_STEP_NORTH_PATH, false),
                Arguments.of("fail ascend south", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_ASCENDING_STEP_SOUTH_PATH, false),
                Arguments.of("fail ascend west", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_ASCENDING_STEP_WEST_PATH, false),
                Arguments.of("fail ascend east", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_ASCENDING_STEP_EAST_PATH, false),
                Arguments.of("fail ascend early northwest", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_ASCENDING_STEP_NORTH_WEST_PATH,
                        false),
                Arguments.of("fail ascend early northeast", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_ASCENDING_STEP_NORTH_EAST_PATH,
                        false),
                Arguments.of("fail ascend early southwest", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_ASCENDING_STEP_SOUTH_WEST_PATH,
                        false),
                Arguments.of("fail ascend early southeast", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_ASCENDING_STEP_SOUTH_EAST_PATH,
                        false),
                Arguments.of("fail ascend late northwest", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_ASCENDING_STEP_NORTH_WEST_PATH,
                        false),
                Arguments.of("fail ascend late northeast", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_ASCENDING_STEP_NORTH_EAST_PATH,
                        false),
                Arguments.of("fail ascend late southwest", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_ASCENDING_STEP_SOUTH_WEST_PATH,
                        false),
                Arguments.of("fail ascend late southeast", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_ASCENDING_STEP_SOUTH_EAST_PATH,
                        false),
                Arguments.of("fail descend north", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_DESCENDING_STEP_NORTH_PATH, false),
                Arguments.of("fail descend south", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_DESCENDING_STEP_SOUTH_PATH, false),
                Arguments.of("fail descend west", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_DESCENDING_STEP_WEST_PATH, false),
                Arguments.of("fail descend east", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_DESCENDING_STEP_EAST_PATH, false),
                Arguments.of("fail descend early northwest", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_DESCENDING_STEP_NORTH_WEST_PATH, false),
                Arguments.of("fail descend early northeast", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_DESCENDING_STEP_NORTH_EAST_PATH, false),
                Arguments.of("fail descend early southwest", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_DESCENDING_STEP_SOUTH_WEST_PATH, false),
                Arguments.of("fail descend early southeast", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_DESCENDING_STEP_SOUTH_EAST_PATH, false),
                Arguments.of("fail descend late northwest", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_DESCENDING_STEP_NORTH_WEST_PATH, false),
                Arguments.of("fail descend late northeast", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_DESCENDING_STEP_NORTH_EAST_PATH, false),
                Arguments.of("fail descend late southwest", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_DESCENDING_STEP_SOUTH_WEST_PATH, false),
                Arguments.of("fail descend late southeast", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_DESCENDING_STEP_SOUTH_EAST_PATH, false),
                Arguments.of(
                        "straight",
                        new String[]{
                                " ",
                                " ",
                                " ",
                        }, new String[]{
                                "?",
                                "1",
                                "!"
                        },
                        true
                ),
                Arguments.of(
                        "diagonal",
                        new String[]{
                                " XX| XX",
                                " XX| XX",
                                "   |   "
                        }, new String[]{
                                "?  |   ",
                                "1  |   ",
                                "23!|   "
                        },
                        true
                ),
                Arguments.of(
                        "corridor",
                        new String[]{
                                "               ",
                                " XXXXXXXXXXXX  ",
                                " X             ",
                                "   XXXXXXXXXXXX",
                                "XXXXXXXXXXXXXXX",
                        }, new String[]{
                                "?123456789abcd ",
                                "             e ",
                                "  qponmlkjihgf ",
                                "  !            ",
                                "               ",
                        },
                        true
                ),
                Arguments.of(
                        "gap",
                        new String[]{
                                "X X|   "
                        }, new String[]{
                                "   |? !"
                        },
                        true
                )
        );
    }

    // Leaping movements are purely vertical (up) and require additional horizontal walking movements
    // The following test cases only attempt to test that the leaping plugin alone is not incorrectly allowing basic movements
    public static Stream<Arguments> nonFunctionalLeapingMovementParameters() {
        return Stream.of(
                Arguments.of("fail flat north", THREE_BY_THREE_CROSS_FLAT_WORLD, SINGLE_FLAT_STEP_NORTH_PATH, false),
                Arguments.of("fail flat south", THREE_BY_THREE_CROSS_FLAT_WORLD, SINGLE_FLAT_STEP_SOUTH_PATH, false),
                Arguments.of("fail flat west", THREE_BY_THREE_CROSS_FLAT_WORLD, SINGLE_FLAT_STEP_WEST_PATH, false),
                Arguments.of("fail flat east", THREE_BY_THREE_CROSS_FLAT_WORLD, SINGLE_FLAT_STEP_EAST_PATH, false),
                Arguments.of("fail flat northwest", THREE_BY_THREE_OPEN_FLAT_WORLD, DIAGONAL_FLAT_STEP_NORTH_WEST_PATH, false),
                Arguments.of("fail flat northeast", THREE_BY_THREE_OPEN_FLAT_WORLD, DIAGONAL_FLAT_STEP_NORTH_EAST_PATH, false),
                Arguments.of("fail flat southwest", THREE_BY_THREE_OPEN_FLAT_WORLD, DIAGONAL_FLAT_STEP_SOUTH_WEST_PATH, false),
                Arguments.of("fail flat southeast", THREE_BY_THREE_OPEN_FLAT_WORLD, DIAGONAL_FLAT_STEP_SOUTH_EAST_PATH, false),
                Arguments.of("fail ascend north", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_ASCENDING_STEP_NORTH_PATH, false),
                Arguments.of("fail ascend south", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_ASCENDING_STEP_SOUTH_PATH, false),
                Arguments.of("fail ascend west", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_ASCENDING_STEP_WEST_PATH, false),
                Arguments.of("fail ascend east", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_ASCENDING_STEP_EAST_PATH, false),
                Arguments.of("fail ascend early northwest", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_ASCENDING_STEP_NORTH_WEST_PATH,
                        false),
                Arguments.of("fail ascend early northeast", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_ASCENDING_STEP_NORTH_EAST_PATH,
                        false),
                Arguments.of("fail ascend early southwest", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_ASCENDING_STEP_SOUTH_WEST_PATH,
                        false),
                Arguments.of("fail ascend early southeast", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_ASCENDING_STEP_SOUTH_EAST_PATH,
                        false),
                Arguments.of("fail ascend late northwest", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_ASCENDING_STEP_NORTH_WEST_PATH,
                        false),
                Arguments.of("fail ascend late northeast", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_ASCENDING_STEP_NORTH_EAST_PATH,
                        false),
                Arguments.of("fail ascend late southwest", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_ASCENDING_STEP_SOUTH_WEST_PATH,
                        false),
                Arguments.of("fail ascend late southeast", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_ASCENDING_STEP_SOUTH_EAST_PATH,
                        false),
                Arguments.of("fail descend north", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_DESCENDING_STEP_NORTH_PATH, false),
                Arguments.of("fail descend south", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_DESCENDING_STEP_SOUTH_PATH, false),
                Arguments.of("fail descend west", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_DESCENDING_STEP_WEST_PATH, false),
                Arguments.of("fail descend east", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_DESCENDING_STEP_EAST_PATH, false),
                Arguments.of("fail descend early northwest", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_DESCENDING_STEP_NORTH_WEST_PATH, false),
                Arguments.of("fail descend early northeast", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_DESCENDING_STEP_NORTH_EAST_PATH, false),
                Arguments.of("fail descend early southwest", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_DESCENDING_STEP_SOUTH_WEST_PATH, false),
                Arguments.of("fail descend early southeast", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_DESCENDING_STEP_SOUTH_EAST_PATH, false),
                Arguments.of("fail descend late northwest", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_DESCENDING_STEP_NORTH_WEST_PATH, false),
                Arguments.of("fail descend late northeast", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_DESCENDING_STEP_NORTH_EAST_PATH, false),
                Arguments.of("fail descend late southwest", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_DESCENDING_STEP_SOUTH_WEST_PATH, false),
                Arguments.of("fail descend late southeast", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_DESCENDING_STEP_SOUTH_EAST_PATH, false)
        );
    }

    // Leaping movements are purely vertical (up) and require additional horizontal walking movements
    // The following test cases attempt to verify that the leaping in combination with the walking plugin correctly allows expected
    // movements
    public static Stream<Arguments> leapingMovementParameters() {
        return Stream.of(
                Arguments.of("succeed flat north", THREE_BY_THREE_CROSS_FLAT_WORLD, SINGLE_FLAT_STEP_NORTH_PATH, true),
                Arguments.of("succeed flat south", THREE_BY_THREE_CROSS_FLAT_WORLD, SINGLE_FLAT_STEP_SOUTH_PATH, true),
                Arguments.of("succeed flat west", THREE_BY_THREE_CROSS_FLAT_WORLD, SINGLE_FLAT_STEP_WEST_PATH, true),
                Arguments.of("succeed flat east", THREE_BY_THREE_CROSS_FLAT_WORLD, SINGLE_FLAT_STEP_EAST_PATH, true),
                Arguments.of("succeed flat northwest", THREE_BY_THREE_OPEN_FLAT_WORLD, DIAGONAL_FLAT_STEP_NORTH_WEST_PATH, true),
                Arguments.of("succeed flat northeast", THREE_BY_THREE_OPEN_FLAT_WORLD, DIAGONAL_FLAT_STEP_NORTH_EAST_PATH, true),
                Arguments.of("succeed flat southwest", THREE_BY_THREE_OPEN_FLAT_WORLD, DIAGONAL_FLAT_STEP_SOUTH_WEST_PATH, true),
                Arguments.of("succeed flat southeast", THREE_BY_THREE_OPEN_FLAT_WORLD, DIAGONAL_FLAT_STEP_SOUTH_EAST_PATH, true),
                Arguments.of("succeed ascend north", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_ASCENDING_STEP_NORTH_PATH, true),
                Arguments.of("succeed ascend south", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_ASCENDING_STEP_SOUTH_PATH, true),
                Arguments.of("succeed ascend west", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_ASCENDING_STEP_WEST_PATH, true),
                Arguments.of("succeed ascend east", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_ASCENDING_STEP_EAST_PATH, true),
                Arguments.of("succeed ascend early northwest", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD,
                        DIAGONAL_ASCENDING_STEP_NORTH_WEST_PATH, true),
                Arguments.of("succeed ascend early northeast", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD,
                        DIAGONAL_ASCENDING_STEP_NORTH_EAST_PATH, true),
                Arguments.of("succeed ascend early southwest", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD,
                        DIAGONAL_ASCENDING_STEP_SOUTH_WEST_PATH, true),
                Arguments.of("succeed ascend early southeast", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD,
                        DIAGONAL_ASCENDING_STEP_SOUTH_EAST_PATH, true),
                Arguments.of("succeed ascend late northwest", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD,
                        DIAGONAL_ASCENDING_STEP_NORTH_WEST_PATH, true),
                Arguments.of("succeed ascend late northeast", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD,
                        DIAGONAL_ASCENDING_STEP_NORTH_EAST_PATH, true),
                Arguments.of("succeed ascend late southwest", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD,
                        DIAGONAL_ASCENDING_STEP_SOUTH_WEST_PATH, true),
                Arguments.of("succeed ascend late southeast", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD,
                        DIAGONAL_ASCENDING_STEP_SOUTH_EAST_PATH, true),
                Arguments.of("fail descend north", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_DESCENDING_STEP_NORTH_PATH, false),
                Arguments.of("fail descend south", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_DESCENDING_STEP_SOUTH_PATH, false),
                Arguments.of("fail descend west", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_DESCENDING_STEP_WEST_PATH, false),
                Arguments.of("fail descend east", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_DESCENDING_STEP_EAST_PATH, false),
                Arguments.of("fail descend early northwest", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_DESCENDING_STEP_NORTH_WEST_PATH, false),
                Arguments.of("fail descend early northeast", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_DESCENDING_STEP_NORTH_EAST_PATH, false),
                Arguments.of("fail descend early southwest", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_DESCENDING_STEP_SOUTH_WEST_PATH, false),
                Arguments.of("fail descend early southeast", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_DESCENDING_STEP_SOUTH_EAST_PATH, false),
                Arguments.of("fail descend late northwest", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_DESCENDING_STEP_NORTH_WEST_PATH, false),
                Arguments.of("fail descend late northeast", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_DESCENDING_STEP_NORTH_EAST_PATH, false),
                Arguments.of("fail descend late southwest", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_DESCENDING_STEP_SOUTH_WEST_PATH, false),
                Arguments.of("fail descend late southeast", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_DESCENDING_STEP_SOUTH_EAST_PATH, false),
                Arguments.of(
                        "one time up",
                        new String[]{
                                " X|  "
                        }, new String[]{
                                "? | !"
                        },
                        true
                ),
                Arguments.of(
                        "two times up",
                        new String[]{
                                " XX|  X|   "
                        }, new String[]{
                                "?  | 1 |  !"
                        },
                        true
                ),
                Arguments.of(
                        "diagonally early up",
                        new String[]{
                                "XX|  ",
                                " X|  "
                        }, new String[]{
                                "  | !",
                                "? |  "
                        },
                        true
                ),
                Arguments.of(
                        "diagonally late up",
                        new String[]{
                                " X|  ",
                                "  |  "
                        }, new String[]{
                                "  | !",
                                "? |  "
                        },
                        true
                ),
                Arguments.of(
                        "leap",
                        new String[]{
                                " XX|   |   |   ",
                                " XX|   |   |   ",
                        }, new String[]{
                                "?  |123|   |   ",
                                "   |  !|   |   ",
                        },
                        true
                ),
                Arguments.of(
                        "three dimensional moves",
                        new String[]{
                                "   |  X|XXX",
                                " X |   |X  ",
                                "   |X X|X  "
                        }, new String[]{
                                "?  |   |   ",
                                "   | 1 |   ",
                                "   |   |  !"
                        },
                        true
                )
        );
    }

    // Falling movements are purely vertical (down) and require additional horizontal walking movements
    // The following test cases only attempt to verify that the falling plugin alone is not incorrectly allowing basic movements
    public static Stream<Arguments> nonFunctionalFallingMovementParameters() {
        return Stream.of(
                Arguments.of("fail flat north", THREE_BY_THREE_CROSS_FLAT_WORLD, SINGLE_FLAT_STEP_NORTH_PATH, false),
                Arguments.of("fail flat south", THREE_BY_THREE_CROSS_FLAT_WORLD, SINGLE_FLAT_STEP_SOUTH_PATH, false),
                Arguments.of("fail flat west", THREE_BY_THREE_CROSS_FLAT_WORLD, SINGLE_FLAT_STEP_WEST_PATH, false),
                Arguments.of("fail flat east", THREE_BY_THREE_CROSS_FLAT_WORLD, SINGLE_FLAT_STEP_EAST_PATH, false),
                Arguments.of("fail flat northwest", THREE_BY_THREE_OPEN_FLAT_WORLD, DIAGONAL_FLAT_STEP_NORTH_WEST_PATH, false),
                Arguments.of("fail flat northeast", THREE_BY_THREE_OPEN_FLAT_WORLD, DIAGONAL_FLAT_STEP_NORTH_EAST_PATH, false),
                Arguments.of("fail flat southwest", THREE_BY_THREE_OPEN_FLAT_WORLD, DIAGONAL_FLAT_STEP_SOUTH_WEST_PATH, false),
                Arguments.of("fail flat southeast", THREE_BY_THREE_OPEN_FLAT_WORLD, DIAGONAL_FLAT_STEP_SOUTH_EAST_PATH, false),
                Arguments.of("fail ascend north", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_ASCENDING_STEP_NORTH_PATH, false),
                Arguments.of("fail ascend south", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_ASCENDING_STEP_SOUTH_PATH, false),
                Arguments.of("fail ascend west", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_ASCENDING_STEP_WEST_PATH, false),
                Arguments.of("fail ascend east", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_ASCENDING_STEP_EAST_PATH, false),
                Arguments.of("fail ascend early northwest", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_ASCENDING_STEP_NORTH_WEST_PATH,
                        false),
                Arguments.of("fail ascend early northeast", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_ASCENDING_STEP_NORTH_EAST_PATH,
                        false),
                Arguments.of("fail ascend early southwest", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_ASCENDING_STEP_SOUTH_WEST_PATH,
                        false),
                Arguments.of("fail ascend early southeast", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_ASCENDING_STEP_SOUTH_EAST_PATH,
                        false),
                Arguments.of("fail ascend late northwest", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_ASCENDING_STEP_NORTH_WEST_PATH,
                        false),
                Arguments.of("fail ascend late northeast", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_ASCENDING_STEP_NORTH_EAST_PATH,
                        false),
                Arguments.of("fail ascend late southwest", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_ASCENDING_STEP_SOUTH_WEST_PATH,
                        false),
                Arguments.of("fail ascend late southeast", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_ASCENDING_STEP_SOUTH_EAST_PATH,
                        false),
                Arguments.of("fail descend north", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_DESCENDING_STEP_NORTH_PATH, false),
                Arguments.of("fail descend south", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_DESCENDING_STEP_SOUTH_PATH, false),
                Arguments.of("fail descend west", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_DESCENDING_STEP_WEST_PATH, false),
                Arguments.of("fail descend east", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_DESCENDING_STEP_EAST_PATH, false),
                Arguments.of("fail descend early northwest", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_DESCENDING_STEP_NORTH_WEST_PATH, false),
                Arguments.of("fail descend early northeast", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_DESCENDING_STEP_NORTH_EAST_PATH, false),
                Arguments.of("fail descend early southwest", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_DESCENDING_STEP_SOUTH_WEST_PATH, false),
                Arguments.of("fail descend early southeast", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_DESCENDING_STEP_SOUTH_EAST_PATH, false),
                Arguments.of("fail descend late northwest", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_DESCENDING_STEP_NORTH_WEST_PATH, false),
                Arguments.of("fail descend late northeast", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_DESCENDING_STEP_NORTH_EAST_PATH, false),
                Arguments.of("fail descend late southwest", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_DESCENDING_STEP_SOUTH_WEST_PATH, false),
                Arguments.of("fail descend late southeast", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_DESCENDING_STEP_SOUTH_EAST_PATH, false)
        );
    }

    // Falling movements are purely vertical (down) and require additional horizontal walking movements
    // The following test cases attempt to verify that the falling in combination with the walking plugin correctly allows expected
    // movements
    public static Stream<Arguments> fallingMovementParameters() {
        return Stream.of(
                Arguments.of("succeed flat north", THREE_BY_THREE_CROSS_FLAT_WORLD, SINGLE_FLAT_STEP_NORTH_PATH, true),
                Arguments.of("succeed flat south", THREE_BY_THREE_CROSS_FLAT_WORLD, SINGLE_FLAT_STEP_SOUTH_PATH, true),
                Arguments.of("succeed flat west", THREE_BY_THREE_CROSS_FLAT_WORLD, SINGLE_FLAT_STEP_WEST_PATH, true),
                Arguments.of("succeed flat east", THREE_BY_THREE_CROSS_FLAT_WORLD, SINGLE_FLAT_STEP_EAST_PATH, true),
                Arguments.of("succeed flat northwest", THREE_BY_THREE_OPEN_FLAT_WORLD, DIAGONAL_FLAT_STEP_NORTH_WEST_PATH, true),
                Arguments.of("succeed flat northeast", THREE_BY_THREE_OPEN_FLAT_WORLD, DIAGONAL_FLAT_STEP_NORTH_EAST_PATH, true),
                Arguments.of("succeed flat southwest", THREE_BY_THREE_OPEN_FLAT_WORLD, DIAGONAL_FLAT_STEP_SOUTH_WEST_PATH, true),
                Arguments.of("succeed flat southeast", THREE_BY_THREE_OPEN_FLAT_WORLD, DIAGONAL_FLAT_STEP_SOUTH_EAST_PATH, true),
                Arguments.of("fail ascend north", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_ASCENDING_STEP_NORTH_PATH, false),
                Arguments.of("fail ascend south", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_ASCENDING_STEP_SOUTH_PATH, false),
                Arguments.of("fail ascend west", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_ASCENDING_STEP_WEST_PATH, false),
                Arguments.of("fail ascend east", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_ASCENDING_STEP_EAST_PATH, false),
                Arguments.of("fail ascend early northwest", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_ASCENDING_STEP_NORTH_WEST_PATH,
                        false),
                Arguments.of("fail ascend early northeast", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_ASCENDING_STEP_NORTH_EAST_PATH,
                        false),
                Arguments.of("fail ascend early southwest", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_ASCENDING_STEP_SOUTH_WEST_PATH,
                        false),
                Arguments.of("fail ascend early southeast", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD, DIAGONAL_ASCENDING_STEP_SOUTH_EAST_PATH,
                        false),
                Arguments.of("fail ascend late northwest", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_ASCENDING_STEP_NORTH_WEST_PATH,
                        false),
                Arguments.of("fail ascend late northeast", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_ASCENDING_STEP_NORTH_EAST_PATH,
                        false),
                Arguments.of("fail ascend late southwest", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_ASCENDING_STEP_SOUTH_WEST_PATH,
                        false),
                Arguments.of("fail ascend late southeast", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD, DIAGONAL_ASCENDING_STEP_SOUTH_EAST_PATH,
                        false),
                Arguments.of("succeed descend north", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_DESCENDING_STEP_NORTH_PATH, true),
                Arguments.of("succeed descend south", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_DESCENDING_STEP_SOUTH_PATH, true),
                Arguments.of("succeed descend west", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_DESCENDING_STEP_WEST_PATH, true),
                Arguments.of("succeed descend east", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_DESCENDING_STEP_EAST_PATH, true),
                Arguments.of("succeed descend early northwest", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD,
                        DIAGONAL_DESCENDING_STEP_NORTH_WEST_PATH, true),
                Arguments.of("succeed descend early northeast", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD,
                        DIAGONAL_DESCENDING_STEP_NORTH_EAST_PATH, true),
                Arguments.of("succeed descend early southwest", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD,
                        DIAGONAL_DESCENDING_STEP_SOUTH_WEST_PATH, true),
                Arguments.of("succeed descend early southeast", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD,
                        DIAGONAL_DESCENDING_STEP_SOUTH_EAST_PATH, true),
                Arguments.of("succeed descend late northwest", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD,
                        DIAGONAL_DESCENDING_STEP_NORTH_WEST_PATH, true),
                Arguments.of("succeed descend late northeast", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD,
                        DIAGONAL_DESCENDING_STEP_NORTH_EAST_PATH, true),
                Arguments.of("succeed descend late southwest", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD,
                        DIAGONAL_DESCENDING_STEP_SOUTH_WEST_PATH, true),
                Arguments.of("succeed descend late southeast", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD,
                        DIAGONAL_DESCENDING_STEP_SOUTH_EAST_PATH, true),
                Arguments.of(
                        "one time down",
                        new String[]{
                                " X|  "
                        }, new String[]{
                                "! | ?"
                        },
                        true
                ),
                Arguments.of(
                        "two times down",
                        new String[]{
                                " XX|  X|   "
                        }, new String[]{
                                "!  | 1 |  ?"
                        },
                        true
                ),
                Arguments.of(
                        "diagonally late down",
                        new String[]{
                                "XX|  ",
                                " X|  "
                        }, new String[]{
                                "  | ?",
                                "! |  "
                        },
                        true
                ),
                Arguments.of(
                        "diagonally early down",
                        new String[]{
                                " X|  ",
                                "  |  "
                        }, new String[]{
                                "  | ?",
                                "! |  "
                        },
                        true
                )
        );
    }

    public static Stream<Arguments> flyingMovementParameters() {
        return Stream.of(
                Arguments.of(
                        "simple wall",
                        new String[]{
                                "   |   |   ",
                                "XXX|XXX|   ",
                                "   |   |   ",
                        }, new String[]{
                                "?  |   |   ",
                                "   |   |   ",
                                "  !|   |   "
                        },
                        true
                )
        );
    }

    public static Stream<Arguments> swimmingMovementParameters() {
        return Stream.of(
                Arguments.of(
                        "straight",
                        new String[]{
                                "~XX",
                                "~XX",
                                "~~~",
                        }, new String[]{
                                "?  ",
                                "1  ",
                                "!  "
                        },
                        true
                ),
                Arguments.of(
                        "diagonal",
                        new String[]{
                                "~XX|~XX",
                                "~XX|~XX",
                                "~~~|~~~"
                        }, new String[]{
                                "?  |   ",
                                "1  |   ",
                                "23!|   "
                        },
                        true
                ),
                Arguments.of(
                        "corridor",
                        new String[]{
                                "~~~~~~~~~~~~~~~",
                                "~XXXXXXXXXXXX~~",
                                "~X~~~~~~~~~~~~~",
                                "~~~XXXXXXXXXXXX",
                                "XXXXXXXXXXXXXXX",
                        }, new String[]{
                                "?123456789abcd ",
                                "             e ",
                                "  qponmlkjihgf ",
                                "  !            ",
                                "               ",
                        },
                        true
                ),
                Arguments.of(
                        "leap",
                        new String[]{
                                "~XX|~~~|~~~",
                                "~XX|~~~|~~~",
                                "~~~|~~~|~~~",
                        }, new String[]{
                                "?  |123|   ",
                                "   |  !|   ",
                                "   |   |   "
                        },
                        true
                ),
                Arguments.of(
                        "three dimensional moves",
                        new String[]{
                                "~~~|~~X|XXX",
                                "~X~|~~~|X~~",
                                "~~~|X~X|X~~"
                        }, new String[]{
                                "?  |   |   ",
                                "   | 1 |   ",
                                "   |   |  !"
                        },
                        true
                )
        );
    }

    public static Stream<Arguments> combinedMovementParameters() {
        return Stream.of(
                Arguments.of(
                        "up and down again",
                        new String[]{
                                " XXX |  X  |     "
                        }, new String[]{
                                "?   !|     |     "
                        },
                        true,
                        new String[]{"walking", "leaping", "falling"}
                ),
                Arguments.of(
                        "down and up again",
                        new String[]{
                                "XX  XX|X    X|      "
                        }, new String[]{
                                "      |      |?    !"
                        },
                        true,
                        new String[]{"walking", "leaping", "falling"}
                ),
                Arguments.of(
                        "jump over",
                        new String[]{
                                " X |   |   |   "
                        }, new String[]{
                                "? !|123|   |   "
                        },
                        true,
                        new String[]{"walking", "leaping", "falling"}
                ),
                Arguments.of(
                        "leap",
                        new String[]{
                                "~XX|XXX",
                                "~XX|   "
                        }, new String[]{
                                "?  |   ",
                                "1  |23!"
                        },
                        true,
                        new String[]{"walking", "leaping", "swimming"}
                ),
                Arguments.of(
                        "three dimensional moves",
                        new String[]{
                                "~~~|~~X|XXX",
                                "~X~|~~~|X  ",
                                "~~~|X~X|X  "
                        }, new String[]{
                                "?  |   |   ",
                                "   | 1 |   ",
                                "   |   |  !"
                        },
                        true,
                        new String[]{"walking", "leaping", "swimming"}
                )
        );
    }

    /*
     * Run test cases of specific movement plugins with all default plugins enabled.
     * This should help catch some inconsistencies.
     */
    public static Stream<Arguments> defaultPluginCombinationParameters() {
        return Stream.of(
                Arguments.of("succeed flat north", THREE_BY_THREE_CROSS_FLAT_WORLD, SINGLE_FLAT_STEP_NORTH_PATH, true),
                Arguments.of("succeed flat south", THREE_BY_THREE_CROSS_FLAT_WORLD, SINGLE_FLAT_STEP_SOUTH_PATH, true),
                Arguments.of("succeed flat west", THREE_BY_THREE_CROSS_FLAT_WORLD, SINGLE_FLAT_STEP_WEST_PATH, true),
                Arguments.of("succeed flat east", THREE_BY_THREE_CROSS_FLAT_WORLD, SINGLE_FLAT_STEP_EAST_PATH, true),
                Arguments.of("succeed flat northwest", THREE_BY_THREE_OPEN_FLAT_WORLD, DIAGONAL_FLAT_STEP_NORTH_WEST_PATH, true),
                Arguments.of("succeed flat northeast", THREE_BY_THREE_OPEN_FLAT_WORLD, DIAGONAL_FLAT_STEP_NORTH_EAST_PATH, true),
                Arguments.of("succeed flat southwest", THREE_BY_THREE_OPEN_FLAT_WORLD, DIAGONAL_FLAT_STEP_SOUTH_WEST_PATH, true),
                Arguments.of("succeed flat southeast", THREE_BY_THREE_OPEN_FLAT_WORLD, DIAGONAL_FLAT_STEP_SOUTH_EAST_PATH, true),
                Arguments.of("succeed ascend north", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_ASCENDING_STEP_NORTH_PATH, true),
                Arguments.of("succeed ascend south", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_ASCENDING_STEP_SOUTH_PATH, true),
                Arguments.of("succeed ascend west", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_ASCENDING_STEP_WEST_PATH, true),
                Arguments.of("succeed ascend east", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_ASCENDING_STEP_EAST_PATH, true),
                Arguments.of("succeed ascend early northwest", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD,
                        DIAGONAL_ASCENDING_STEP_NORTH_WEST_PATH, true),
                Arguments.of("succeed ascend early northeast", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD,
                        DIAGONAL_ASCENDING_STEP_NORTH_EAST_PATH, true),
                Arguments.of("succeed ascend early southwest", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD,
                        DIAGONAL_ASCENDING_STEP_SOUTH_WEST_PATH, true),
                Arguments.of("succeed ascend early southeast", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD,
                        DIAGONAL_ASCENDING_STEP_SOUTH_EAST_PATH, true),
                Arguments.of("succeed ascend late northwest", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD,
                        DIAGONAL_ASCENDING_STEP_NORTH_WEST_PATH, true),
                Arguments.of("succeed ascend late northeast", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD,
                        DIAGONAL_ASCENDING_STEP_NORTH_EAST_PATH, true),
                Arguments.of("succeed ascend late southwest", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD,
                        DIAGONAL_ASCENDING_STEP_SOUTH_WEST_PATH, true),
                Arguments.of("succeed ascend late southeast", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD,
                        DIAGONAL_ASCENDING_STEP_SOUTH_EAST_PATH, true),
                Arguments.of("succeed descend north", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_DESCENDING_STEP_NORTH_PATH, true),
                Arguments.of("succeed descend south", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_DESCENDING_STEP_SOUTH_PATH, true),
                Arguments.of("succeed descend west", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_DESCENDING_STEP_WEST_PATH, true),
                Arguments.of("succeed descend east", THREE_BY_THREE_CROSS_ASCENDING_OUT_WORLD, SINGLE_DESCENDING_STEP_EAST_PATH, true),
                Arguments.of("succeed descend early northwest", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD,
                        DIAGONAL_DESCENDING_STEP_NORTH_WEST_PATH, true),
                Arguments.of("succeed descend early northeast", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD,
                        DIAGONAL_DESCENDING_STEP_NORTH_EAST_PATH, true),
                Arguments.of("succeed descend early southwest", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD,
                        DIAGONAL_DESCENDING_STEP_SOUTH_WEST_PATH, true),
                Arguments.of("succeed descend early southeast", THREE_BY_THREE_OPEN_ASCENDING_CORNER_OUT_WORLD,
                        DIAGONAL_DESCENDING_STEP_SOUTH_EAST_PATH, true),
                Arguments.of("succeed descend late northwest", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD,
                        DIAGONAL_DESCENDING_STEP_NORTH_WEST_PATH, true),
                Arguments.of("succeed descend late northeast", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD,
                        DIAGONAL_DESCENDING_STEP_NORTH_EAST_PATH, true),
                Arguments.of("succeed descend late southwest", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD,
                        DIAGONAL_DESCENDING_STEP_SOUTH_WEST_PATH, true),
                Arguments.of("succeed descend late southeast", THREE_BY_THREE_OPEN_ASCENDING_FULL_OUT_WORLD,
                        DIAGONAL_DESCENDING_STEP_SOUTH_EAST_PATH, true),
                Arguments.of(
                        "one time up",
                        new String[]{
                                " X|  "
                        }, new String[]{
                                "? | !"
                        },
                        true
                ),
                Arguments.of(
                        "one time down",
                        new String[]{
                                " X|  "
                        }, new String[]{
                                "! | ?"
                        },
                        true
                ),
                Arguments.of(
                        "two times up",
                        new String[]{
                                " XX|  X|   "
                        }, new String[]{
                                "?  | 1 |  !"
                        },
                        true
                ),
                Arguments.of(
                        "two times down",
                        new String[]{
                                " XX|  X|   "
                        }, new String[]{
                                "!  | 1 |  ?"
                        },
                        true
                ),
                Arguments.of(
                        "diagonally early up",
                        new String[]{
                                "XX|  ",
                                " X|  "
                        }, new String[]{
                                "  | !",
                                "? |  "
                        },
                        true
                ),
                Arguments.of(
                        "diagonally late up",
                        new String[]{
                                " X|  ",
                                "  |  "
                        }, new String[]{
                                "  | !",
                                "? |  "
                        },
                        true
                ),
                Arguments.of(
                        "diagonally late down",
                        new String[]{
                                "XX|  ",
                                " X|  "
                        }, new String[]{
                                "  | ?",
                                "! |  "
                        },
                        true
                ),
                Arguments.of(
                        "diagonally early down",
                        new String[]{
                                " X|  ",
                                "  |  "
                        }, new String[]{
                                "  | ?",
                                "! |  "
                        },
                        true
                ),
                Arguments.of(
                        "gap",
                        new String[]{
                                "X X|   "
                        }, new String[]{
                                "   |? !"
                        },
                        true
                )
//                TODO: Re-enable this test and fix the underlying movement behavior
//                Arguments.of(
//                        "jump over",
//                        new String[]{
//                                " X |   |   |   "
//                        }, new String[]{
//                                "? !|123|   |   "
//                        },
//                        true
//                )
        );
    }

    @AfterEach
    void clean() {
        entity.destroy();
    }

    @MethodSource("walkingMovementParameters")
    @ParameterizedTest(name = "walking: {0}")
    @DisplayName("Test movement plugin for walking")
    void testWalkingMovement(String name, String[] world, String[] path, boolean successExpected) {
        runTest(name, world, path, successExpected, "walking");
    }

    @MethodSource("nonFunctionalLeapingMovementParameters")
    @ParameterizedTest(name = "leaping: {0}")
    @DisplayName("Test movement plugin for leaping (intentionally without walking)")
    void testNonFunctionalLeapingMovement(String name, String[] world, String[] path, boolean successExpected) {
        runTest(name, world, path, successExpected, "leaping");
    }

    @MethodSource("leapingMovementParameters")
    @ParameterizedTest(name = "walking, leaping: {0}")
    @DisplayName("Test movement plugin for leaping (requires walking)")
    void testLeapingMovement(String name, String[] world, String[] path, boolean successExpected) {
        runTest(name, world, path, successExpected, "walking", "leaping");
    }

    @MethodSource("nonFunctionalFallingMovementParameters")
    @ParameterizedTest(name = "falling: {0}")
    @DisplayName("Test movement plugin for falling (intentionally without walking)")
    void testNonFunctionalFallingMovement(String name, String[] world, String[] path, boolean successExpected) {
        runTest(name, world, path, successExpected, "falling");
    }

    @MethodSource("fallingMovementParameters")
    @ParameterizedTest(name = "walking, falling: {0}")
    @DisplayName("Test movement plugin for falling (requires walking)")
    void testFallingMovement(String name, String[] world, String[] path, boolean successExpected) {
        runTest(name, world, path, successExpected, "walking", "falling");
    }

    @MethodSource("flyingMovementParameters")
    @ParameterizedTest(name = "flying: {0}")
    @DisplayName("Test movement plugin for flying")
    void testFlyingMovement(String name, String[] world, String[] path, boolean successExpected) {
        runTest(name, world, path, successExpected, "flying");
    }

    @MethodSource("swimmingMovementParameters")
    @ParameterizedTest(name = "swimming: {0}")
    @DisplayName("Test movement plugin for swimming")
    void testSwimmingMovement(String name, String[] world, String[] path, boolean successExpected) {
        runTest(name, world, path, successExpected, "swimming");
    }

    @MethodSource("combinedMovementParameters")
    @ParameterizedTest(name = "{4}: {0}")
    @DisplayName("Test movement plugin combinations")
    void testCombinedMovement(String name, String[] world, String[] path, boolean successExpected, String... movementTypes) {
        runTest(name, world, path, successExpected, movementTypes);
    }

    @MethodSource("defaultPluginCombinationParameters")
    @ParameterizedTest(name = "default: {0}")
    @DisplayName("Test default movement plugin combinations for comparison")
    void testDefaultMovement(String name, String[] world, String[] path, boolean successExpected) {
        runTest(name, world, path, successExpected, DEFAULT_MOVEMENT_MODES);
    }

    void runTest(String name, String[] world, String[] path, boolean successExpected, String... movementTypes) {
        // This will skip all tests where we expect a timeout because the character should not move.
        // Waiting for all these timeouts adds a lot to the total execution time.
        // We keep the tests in here for now, but skip them by default to be gentle on the CI.
        //TODO: move tests to ensure paths exist/don't exist to FlexiblePathfinding
        Assumptions.assumeTrue(successExpected);

        setupWorld(world, AIR_HEIGHT);

        // find start and goal positions from path data
        Vector3i start = new Vector3i();
        Vector3i stop = new Vector3i();
        detectPath(path, AIR_HEIGHT, start, stop);

        logger.info("movement plugin combination: {}", Lists.newArrayList(movementTypes));

        entity = createMovingCharacter(CHAR_HEIGHT, CHAR_RADIUS, start, stop, movementTypes);

        helper.runUntil(() -> Blocks.toBlockPos(entity.getComponent(LocationComponent.class)
                .getWorldPosition(new Vector3f())).distance(start) <= 0.5f);

        boolean timedOut = helper.runWhile(TIMEOUT, () -> {
            Vector3f pos = entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f());
            return Blocks.toBlockPos(pos).distance(stop) > 0;
        });

        Vector3i currentPos = Blocks.toBlockPos(entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f()));
        if (successExpected) {
            Assertions.assertEquals(stop, currentPos, () -> printTest("Test character is not at target position.", world, start, stop,
                    entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f())));
            Assertions.assertFalse(timedOut,
                    () -> String.format("Timeout during character movement (start: %s, target: %s, position: %s)",
                            start, stop, currentPos));
        } else {
            Assertions.assertEquals(start, currentPos, "Test character should be at start position but has moved.");
        }
    }

    private String printTest(String msg, String[] world, Vector3i start, Vector3i stop, Vector3f pos) {
        return msg + "\n" +
                "  start   : " + start + "\n" +
                "  target  : " + stop + "\n" +
                "  current : " + pos + "\n\n" +
                Arrays.stream(world).map(s -> "  " + s).collect(Collectors.joining("\n")) +
                "\n";
    }

    private EntityRef createMovingCharacter(float height, float radius, Vector3i start, Vector3i stop, String... movementTypes) {
        EntityBuilder builder = entityManager.newBuilder("Behaviors:testCharacter");
        builder.setSendLifecycleEvents(true);
        builder.upsertComponent(MinionMoveComponent.class, maybeComponent -> {
            MinionMoveComponent moveComponent = maybeComponent.orElse(new MinionMoveComponent());
            moveComponent.setPathGoal(stop);
            moveComponent.movementTypes.clear();
            moveComponent.movementTypes.addAll(Sets.newHashSet(movementTypes));
            return moveComponent;
        });
        builder.updateComponent(CharacterMovementComponent.class, characterMovement -> {
            characterMovement.height = height;
            characterMovement.radius = radius;
            return characterMovement;
        });
        builder.updateComponent(LocationComponent.class, location -> {
            location.setWorldPosition(new Vector3f(start));
            return location;
        });

        EntityRef character = builder.build();
        physicsEngine.recomputeCharacterCollider(character);

        return character;
    }

    /**
     * Detect path for entity at map {@code path}
     *
     * @param path map with path
     * @param airHeight air height for world
     * @param start (?) ref parameter - set start point
     * @param stop (!) ref parameter - set end point
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

        helper.runUntil(helper.makeBlocksRelevant(extents));

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
                    case ' ':
                        worldProvider.setBlock(new Vector3i(x, y, z), air);
                        x += 1;
                        break;
                    case 'X':
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
