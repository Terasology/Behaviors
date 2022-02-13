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
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityBuilder;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
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
import org.terasology.moduletestingenvironment.MTEExtension;
import org.terasology.moduletestingenvironment.ModuleTestingHelper;
import org.terasology.moduletestingenvironment.extension.Dependencies;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Dependencies("Behaviors")
@Tag("MteTest")
@ExtendWith(MTEExtension.class)
public class MovementTests {
    private static final Logger logger = LoggerFactory.getLogger(MovementTests.class);

    private static final long TIMEOUT = 10_000;
    private static final int AIR_HEIGHT = 41;
    private static final float CHAR_HEIGHT = 0.9f;
    private static final float CHAR_RADIUS = 0.3f;
    private static final String[] DEFAULT_MOVEMENT_MODES = {"walking", "leaping", "falling"};

    private static final String[] threeByThreeCrossFlatWorld = {
            " X ",
            "XXX",
            " X "
    };

    private static final String[] threeByThreeOpenFlatWorld = {
            "XXX",
            "XXX",
            "XXX"
    };

    private static final String[] threeByThreeCrossAscendingOutWorld = {
            "   | X ",
            " X |XXX",
            "   | X "
    };

    private static final String[] threeByThreeOpenAscendingCornerOutWorld = {
            " X |XXX",
            "XXX|XXX",
            " X |XXX"
    };

    private static final String[] threeByThreeOpenAscendingFullOutWorld = {
            "   |XXX",
            " X |XXX",
            "   |XXX"
    };

    private static final String[] singleFlatStepNorthPath = {
            " ! ",
            " ? ",
            "   "
    };

    private static final String[] singleFlatStepSouthPath = {
            "   ",
            " ? ",
            " ! "
    };

    private static final String[] singleFlatStepWestPath = {
            "   ",
            "!? ",
            "   "
    };

    private static final String[] singleFlatStepEastPath = {
            "   ",
            " ?!",
            "   "
    };

    private static final String[] diagonalFlatStepNorthWestPath = {
            "!  ",
            " ? ",
            "   "
    };

    private static final String[] diagonalFlatStepNorthEastPath = {
            "  !",
            " ? ",
            "   "
    };

    private static final String[] diagonalFlatStepSouthWestPath = {
            "   ",
            " ? ",
            "!  "
    };

    private static final String[] diagonalFlatStepSouthEastPath = {
            "   ",
            " ? ",
            "  !"
    };

    private static final String[] singleAscendingStepNorthPath = {
            "   | ! ",
            " ? |   ",
            "   |   "
    };

    private static final String[] singleAscendingStepSouthPath = {
            "   |   ",
            " ? |   ",
            "   | ! "
    };

    private static final String[] singleAscendingStepWestPath = {
            "   |   ",
            " ? |!  ",
            "   |   "
    };

    private static final String[] singleAscendingStepEastPath = {
            "   |   ",
            " ? |  !",
            "   |   "
    };

    private static final String[] diagonalAscendingStepNorthWestPath = {
            "   |!  ",
            " ? |   ",
            "   |   "
    };

    private static final String[] diagonalAscendingStepNorthEastPath = {
            "   |  !",
            " ? |   ",
            "   |   "
    };

    private static final String[] diagonalAscendingStepSouthWestPath = {
            "   |   ",
            " ? |   ",
            "   |!  "
    };

    private static final String[] diagonalAscendingStepSouthEastPath = {
            "   |   ",
            " ? |   ",
            "   |  !"
    };

    private static final String[] singleDescendingStepNorthPath = {
            "   |   ",
            " ! |   ",
            "   | ? "
    };

    private static final String[] singleDescendingStepSouthPath = {
            "   | ? ",
            " ! |   ",
            "   |   "
    };

    private static final String[] singleDescendingStepWestPath = {
            "   |   ",
            " ! |  ?",
            "   |   "
    };

    private static final String[] singleDescendingStepEastPath = {
            "   |   ",
            " ! |?  ",
            "   |   "
    };

    private static final String[] diagonalDescendingStepNorthWestPath = {
            "   |   ",
            " ! |   ",
            "   |  ?"
    };

    private static final String[] diagonalDescendingStepNorthEastPath = {
            "   |   ",
            " ! |   ",
            "   |?  "
    };

    private static final String[] diagonalDescendingStepSouthWestPath = {
            "   |  ?",
            " ! |   ",
            "   |   "
    };

    private static final String[] diagonalDescendingStepSouthEastPath = {
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
                Arguments.of("succeed flat north", threeByThreeCrossFlatWorld, singleFlatStepNorthPath, true),
                Arguments.of("succeed flat south", threeByThreeCrossFlatWorld, singleFlatStepSouthPath, true),
                Arguments.of("succeed flat west", threeByThreeCrossFlatWorld, singleFlatStepWestPath, true),
                Arguments.of("succeed flat east", threeByThreeCrossFlatWorld, singleFlatStepEastPath, true),
                Arguments.of("succeed flat northwest", threeByThreeOpenFlatWorld, diagonalFlatStepNorthWestPath, true),
                Arguments.of("succeed flat northeast", threeByThreeOpenFlatWorld, diagonalFlatStepNorthEastPath, true),
                Arguments.of("succeed flat southwest", threeByThreeOpenFlatWorld, diagonalFlatStepSouthWestPath, true),
                Arguments.of("succeed flat southeast", threeByThreeOpenFlatWorld, diagonalFlatStepSouthEastPath, true),
                Arguments.of("fail ascend north", threeByThreeCrossAscendingOutWorld, singleAscendingStepNorthPath, false),
                Arguments.of("fail ascend south", threeByThreeCrossAscendingOutWorld, singleAscendingStepSouthPath, false),
                Arguments.of("fail ascend west", threeByThreeCrossAscendingOutWorld, singleAscendingStepWestPath, false),
                Arguments.of("fail ascend east", threeByThreeCrossAscendingOutWorld, singleAscendingStepEastPath, false),
                Arguments.of("fail ascend early northwest", threeByThreeOpenAscendingFullOutWorld, diagonalAscendingStepNorthWestPath,
                        false),
                Arguments.of("fail ascend early northeast", threeByThreeOpenAscendingFullOutWorld, diagonalAscendingStepNorthEastPath,
                        false),
                Arguments.of("fail ascend early southwest", threeByThreeOpenAscendingFullOutWorld, diagonalAscendingStepSouthWestPath,
                        false),
                Arguments.of("fail ascend early southeast", threeByThreeOpenAscendingFullOutWorld, diagonalAscendingStepSouthEastPath,
                        false),
                Arguments.of("fail ascend late northwest", threeByThreeOpenAscendingCornerOutWorld, diagonalAscendingStepNorthWestPath,
                        false),
                Arguments.of("fail ascend late northeast", threeByThreeOpenAscendingCornerOutWorld, diagonalAscendingStepNorthEastPath,
                        false),
                Arguments.of("fail ascend late southwest", threeByThreeOpenAscendingCornerOutWorld, diagonalAscendingStepSouthWestPath,
                        false),
                Arguments.of("fail ascend late southeast", threeByThreeOpenAscendingCornerOutWorld, diagonalAscendingStepSouthEastPath,
                        false),
                Arguments.of("fail descend north", threeByThreeCrossAscendingOutWorld, singleDescendingStepNorthPath, false),
                Arguments.of("fail descend south", threeByThreeCrossAscendingOutWorld, singleDescendingStepSouthPath, false),
                Arguments.of("fail descend west", threeByThreeCrossAscendingOutWorld, singleDescendingStepWestPath, false),
                Arguments.of("fail descend east", threeByThreeCrossAscendingOutWorld, singleDescendingStepEastPath, false),
                Arguments.of("fail descend early northwest", threeByThreeOpenAscendingCornerOutWorld, diagonalDescendingStepNorthWestPath, false),
                Arguments.of("fail descend early northeast", threeByThreeOpenAscendingCornerOutWorld, diagonalDescendingStepNorthEastPath, false),
                Arguments.of("fail descend early southwest", threeByThreeOpenAscendingCornerOutWorld, diagonalDescendingStepSouthWestPath, false),
                Arguments.of("fail descend early southeast", threeByThreeOpenAscendingCornerOutWorld, diagonalDescendingStepSouthEastPath, false),
                Arguments.of("fail descend late northwest", threeByThreeOpenAscendingFullOutWorld, diagonalDescendingStepNorthWestPath, false),
                Arguments.of("fail descend late northeast", threeByThreeOpenAscendingFullOutWorld, diagonalDescendingStepNorthEastPath, false),
                Arguments.of("fail descend late southwest", threeByThreeOpenAscendingFullOutWorld, diagonalDescendingStepSouthWestPath, false),
                Arguments.of("fail descend late southeast", threeByThreeOpenAscendingFullOutWorld, diagonalDescendingStepSouthEastPath, false),
                Arguments.of(
                        "straight",
                        new String[]{
                                "X",
                                "X",
                                "X",
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
                                "X  |X  ",
                                "X  |X  ",
                                "XXX|XXX"
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
                        true
                ),
                Arguments.of(
                        "gap",
                        new String[]{
                                " X |XXX"
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
                Arguments.of("fail flat north", threeByThreeCrossFlatWorld, singleFlatStepNorthPath, false),
                Arguments.of("fail flat south", threeByThreeCrossFlatWorld, singleFlatStepSouthPath, false),
                Arguments.of("fail flat west", threeByThreeCrossFlatWorld, singleFlatStepWestPath, false),
                Arguments.of("fail flat east", threeByThreeCrossFlatWorld, singleFlatStepEastPath, false),
                Arguments.of("fail flat northwest", threeByThreeOpenFlatWorld, diagonalFlatStepNorthWestPath, false),
                Arguments.of("fail flat northeast", threeByThreeOpenFlatWorld, diagonalFlatStepNorthEastPath, false),
                Arguments.of("fail flat southwest", threeByThreeOpenFlatWorld, diagonalFlatStepSouthWestPath, false),
                Arguments.of("fail flat southeast", threeByThreeOpenFlatWorld, diagonalFlatStepSouthEastPath, false),
                Arguments.of("fail ascend north", threeByThreeCrossAscendingOutWorld, singleAscendingStepNorthPath, false),
                Arguments.of("fail ascend south", threeByThreeCrossAscendingOutWorld, singleAscendingStepSouthPath, false),
                Arguments.of("fail ascend west", threeByThreeCrossAscendingOutWorld, singleAscendingStepWestPath, false),
                Arguments.of("fail ascend east", threeByThreeCrossAscendingOutWorld, singleAscendingStepEastPath, false),
                Arguments.of("fail ascend early northwest", threeByThreeOpenAscendingFullOutWorld, diagonalAscendingStepNorthWestPath,
                        false),
                Arguments.of("fail ascend early northeast", threeByThreeOpenAscendingFullOutWorld, diagonalAscendingStepNorthEastPath,
                        false),
                Arguments.of("fail ascend early southwest", threeByThreeOpenAscendingFullOutWorld, diagonalAscendingStepSouthWestPath,
                        false),
                Arguments.of("fail ascend early southeast", threeByThreeOpenAscendingFullOutWorld, diagonalAscendingStepSouthEastPath,
                        false),
                Arguments.of("fail ascend late northwest", threeByThreeOpenAscendingCornerOutWorld, diagonalAscendingStepNorthWestPath,
                        false),
                Arguments.of("fail ascend late northeast", threeByThreeOpenAscendingCornerOutWorld, diagonalAscendingStepNorthEastPath,
                        false),
                Arguments.of("fail ascend late southwest", threeByThreeOpenAscendingCornerOutWorld, diagonalAscendingStepSouthWestPath,
                        false),
                Arguments.of("fail ascend late southeast", threeByThreeOpenAscendingCornerOutWorld, diagonalAscendingStepSouthEastPath,
                        false),
                Arguments.of("fail descend north", threeByThreeCrossAscendingOutWorld, singleDescendingStepNorthPath, false),
                Arguments.of("fail descend south", threeByThreeCrossAscendingOutWorld, singleDescendingStepSouthPath, false),
                Arguments.of("fail descend west", threeByThreeCrossAscendingOutWorld, singleDescendingStepWestPath, false),
                Arguments.of("fail descend east", threeByThreeCrossAscendingOutWorld, singleDescendingStepEastPath, false),
                Arguments.of("fail descend early northwest", threeByThreeOpenAscendingCornerOutWorld, diagonalDescendingStepNorthWestPath, false),
                Arguments.of("fail descend early northeast", threeByThreeOpenAscendingCornerOutWorld, diagonalDescendingStepNorthEastPath, false),
                Arguments.of("fail descend early southwest", threeByThreeOpenAscendingCornerOutWorld, diagonalDescendingStepSouthWestPath, false),
                Arguments.of("fail descend early southeast", threeByThreeOpenAscendingCornerOutWorld, diagonalDescendingStepSouthEastPath, false),
                Arguments.of("fail descend late northwest", threeByThreeOpenAscendingFullOutWorld, diagonalDescendingStepNorthWestPath, false),
                Arguments.of("fail descend late northeast", threeByThreeOpenAscendingFullOutWorld, diagonalDescendingStepNorthEastPath, false),
                Arguments.of("fail descend late southwest", threeByThreeOpenAscendingFullOutWorld, diagonalDescendingStepSouthWestPath, false),
                Arguments.of("fail descend late southeast", threeByThreeOpenAscendingFullOutWorld, diagonalDescendingStepSouthEastPath, false)
        );
    }

    // Leaping movements are purely vertical (up) and require additional horizontal walking movements
    // The following test cases attempt to verify that the leaping in combination with the walking plugin correctly allows expected
    // movements
    public static Stream<Arguments> leapingMovementParameters() {
        return Stream.of(
                Arguments.of("succeed flat north", threeByThreeCrossFlatWorld, singleFlatStepNorthPath, true),
                Arguments.of("succeed flat south", threeByThreeCrossFlatWorld, singleFlatStepSouthPath, true),
                Arguments.of("succeed flat west", threeByThreeCrossFlatWorld, singleFlatStepWestPath, true),
                Arguments.of("succeed flat east", threeByThreeCrossFlatWorld, singleFlatStepEastPath, true),
                Arguments.of("succeed flat northwest", threeByThreeOpenFlatWorld, diagonalFlatStepNorthWestPath, true),
                Arguments.of("succeed flat northeast", threeByThreeOpenFlatWorld, diagonalFlatStepNorthEastPath, true),
                Arguments.of("succeed flat southwest", threeByThreeOpenFlatWorld, diagonalFlatStepSouthWestPath, true),
                Arguments.of("succeed flat southeast", threeByThreeOpenFlatWorld, diagonalFlatStepSouthEastPath, true),
                Arguments.of("succeed ascend north", threeByThreeCrossAscendingOutWorld, singleAscendingStepNorthPath, true),
                Arguments.of("succeed ascend south", threeByThreeCrossAscendingOutWorld, singleAscendingStepSouthPath, true),
                Arguments.of("succeed ascend west", threeByThreeCrossAscendingOutWorld, singleAscendingStepWestPath, true),
                Arguments.of("succeed ascend east", threeByThreeCrossAscendingOutWorld, singleAscendingStepEastPath, true),
                Arguments.of("succeed ascend early northwest", threeByThreeOpenAscendingFullOutWorld,
                        diagonalAscendingStepNorthWestPath, true),
                Arguments.of("succeed ascend early northeast", threeByThreeOpenAscendingFullOutWorld,
                        diagonalAscendingStepNorthEastPath, true),
                Arguments.of("succeed ascend early southwest", threeByThreeOpenAscendingFullOutWorld,
                        diagonalAscendingStepSouthWestPath, true),
                Arguments.of("succeed ascend early southeast", threeByThreeOpenAscendingFullOutWorld,
                        diagonalAscendingStepSouthEastPath, true),
                Arguments.of("succeed ascend late northwest", threeByThreeOpenAscendingCornerOutWorld,
                        diagonalAscendingStepNorthWestPath, true),
                Arguments.of("succeed ascend late northeast", threeByThreeOpenAscendingCornerOutWorld,
                        diagonalAscendingStepNorthEastPath, true),
                Arguments.of("succeed ascend late southwest", threeByThreeOpenAscendingCornerOutWorld,
                        diagonalAscendingStepSouthWestPath, true),
                Arguments.of("succeed ascend late southeast", threeByThreeOpenAscendingCornerOutWorld,
                        diagonalAscendingStepSouthEastPath, true),
                Arguments.of("fail descend north", threeByThreeCrossAscendingOutWorld, singleDescendingStepNorthPath, false),
                Arguments.of("fail descend south", threeByThreeCrossAscendingOutWorld, singleDescendingStepSouthPath, false),
                Arguments.of("fail descend west", threeByThreeCrossAscendingOutWorld, singleDescendingStepWestPath, false),
                Arguments.of("fail descend east", threeByThreeCrossAscendingOutWorld, singleDescendingStepEastPath, false),
                Arguments.of("fail descend early northwest", threeByThreeOpenAscendingCornerOutWorld, diagonalDescendingStepNorthWestPath, false),
                Arguments.of("fail descend early northeast", threeByThreeOpenAscendingCornerOutWorld, diagonalDescendingStepNorthEastPath, false),
                Arguments.of("fail descend early southwest", threeByThreeOpenAscendingCornerOutWorld, diagonalDescendingStepSouthWestPath, false),
                Arguments.of("fail descend early southeast", threeByThreeOpenAscendingCornerOutWorld, diagonalDescendingStepSouthEastPath, false),
                Arguments.of("fail descend late northwest", threeByThreeOpenAscendingFullOutWorld, diagonalDescendingStepNorthWestPath, false),
                Arguments.of("fail descend late northeast", threeByThreeOpenAscendingFullOutWorld, diagonalDescendingStepNorthEastPath, false),
                Arguments.of("fail descend late southwest", threeByThreeOpenAscendingFullOutWorld, diagonalDescendingStepSouthWestPath, false),
                Arguments.of("fail descend late southeast", threeByThreeOpenAscendingFullOutWorld, diagonalDescendingStepSouthEastPath, false),
                Arguments.of(
                        "one time up",
                        new String[]{
                                "X |XX"
                        }, new String[]{
                                "? | !"
                        },
                        true
                ),
                Arguments.of(
                        "two times up",
                        new String[]{
                                "X  |XX |XXX"
                        }, new String[]{
                                "?  | 1 |  !"
                        },
                        true
                ),
                Arguments.of(
                        "diagonally early up",
                        new String[]{
                                "  |XX",
                                "X |XX"
                        }, new String[]{
                                "  | !",
                                "? |  "
                        },
                        true
                ),
                Arguments.of(
                        "diagonally late up",
                        new String[]{
                                "X |XX",
                                "XX|XX"
                        }, new String[]{
                                "  | !",
                                "? |  "
                        },
                        true
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
                        true
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
                        true
                )
        );
    }

    // Falling movements are purely vertical (down) and require additional horizontal walking movements
    // The following test cases only attempt to verify that the falling plugin alone is not incorrectly allowing basic movements
    public static Stream<Arguments> nonFunctionalFallingMovementParameters() {
        return Stream.of(
                Arguments.of("fail flat north", threeByThreeCrossFlatWorld, singleFlatStepNorthPath, false),
                Arguments.of("fail flat south", threeByThreeCrossFlatWorld, singleFlatStepSouthPath, false),
                Arguments.of("fail flat west", threeByThreeCrossFlatWorld, singleFlatStepWestPath, false),
                Arguments.of("fail flat east", threeByThreeCrossFlatWorld, singleFlatStepEastPath, false),
                Arguments.of("fail flat northwest", threeByThreeOpenFlatWorld, diagonalFlatStepNorthWestPath, false),
                Arguments.of("fail flat northeast", threeByThreeOpenFlatWorld, diagonalFlatStepNorthEastPath, false),
                Arguments.of("fail flat southwest", threeByThreeOpenFlatWorld, diagonalFlatStepSouthWestPath, false),
                Arguments.of("fail flat southeast", threeByThreeOpenFlatWorld, diagonalFlatStepSouthEastPath, false),
                Arguments.of("fail ascend north", threeByThreeCrossAscendingOutWorld, singleAscendingStepNorthPath, false),
                Arguments.of("fail ascend south", threeByThreeCrossAscendingOutWorld, singleAscendingStepSouthPath, false),
                Arguments.of("fail ascend west", threeByThreeCrossAscendingOutWorld, singleAscendingStepWestPath, false),
                Arguments.of("fail ascend east", threeByThreeCrossAscendingOutWorld, singleAscendingStepEastPath, false),
                Arguments.of("fail ascend early northwest", threeByThreeOpenAscendingFullOutWorld, diagonalAscendingStepNorthWestPath,
                        false),
                Arguments.of("fail ascend early northeast", threeByThreeOpenAscendingFullOutWorld, diagonalAscendingStepNorthEastPath,
                        false),
                Arguments.of("fail ascend early southwest", threeByThreeOpenAscendingFullOutWorld, diagonalAscendingStepSouthWestPath,
                        false),
                Arguments.of("fail ascend early southeast", threeByThreeOpenAscendingFullOutWorld, diagonalAscendingStepSouthEastPath,
                        false),
                Arguments.of("fail ascend late northwest", threeByThreeOpenAscendingCornerOutWorld, diagonalAscendingStepNorthWestPath,
                        false),
                Arguments.of("fail ascend late northeast", threeByThreeOpenAscendingCornerOutWorld, diagonalAscendingStepNorthEastPath,
                        false),
                Arguments.of("fail ascend late southwest", threeByThreeOpenAscendingCornerOutWorld, diagonalAscendingStepSouthWestPath,
                        false),
                Arguments.of("fail ascend late southeast", threeByThreeOpenAscendingCornerOutWorld, diagonalAscendingStepSouthEastPath,
                        false),
                Arguments.of("fail descend north", threeByThreeCrossAscendingOutWorld, singleDescendingStepNorthPath, false),
                Arguments.of("fail descend south", threeByThreeCrossAscendingOutWorld, singleDescendingStepSouthPath, false),
                Arguments.of("fail descend west", threeByThreeCrossAscendingOutWorld, singleDescendingStepWestPath, false),
                Arguments.of("fail descend east", threeByThreeCrossAscendingOutWorld, singleDescendingStepEastPath, false),
                Arguments.of("fail descend early northwest", threeByThreeOpenAscendingCornerOutWorld, diagonalDescendingStepNorthWestPath, false),
                Arguments.of("fail descend early northeast", threeByThreeOpenAscendingCornerOutWorld, diagonalDescendingStepNorthEastPath, false),
                Arguments.of("fail descend early southwest", threeByThreeOpenAscendingCornerOutWorld, diagonalDescendingStepSouthWestPath, false),
                Arguments.of("fail descend early southeast", threeByThreeOpenAscendingCornerOutWorld, diagonalDescendingStepSouthEastPath, false),
                Arguments.of("fail descend late northwest", threeByThreeOpenAscendingFullOutWorld, diagonalDescendingStepNorthWestPath, false),
                Arguments.of("fail descend late northeast", threeByThreeOpenAscendingFullOutWorld, diagonalDescendingStepNorthEastPath, false),
                Arguments.of("fail descend late southwest", threeByThreeOpenAscendingFullOutWorld, diagonalDescendingStepSouthWestPath, false),
                Arguments.of("fail descend late southeast", threeByThreeOpenAscendingFullOutWorld, diagonalDescendingStepSouthEastPath, false)
        );
    }

    // Falling movements are purely vertical (down) and require additional horizontal walking movements
    // The following test cases attempt to verify that the falling in combination with the walking plugin correctly allows expected
    // movements
    public static Stream<Arguments> fallingMovementParameters() {
        return Stream.of(
                Arguments.of("succeed flat north", threeByThreeCrossFlatWorld, singleFlatStepNorthPath, true),
                Arguments.of("succeed flat south", threeByThreeCrossFlatWorld, singleFlatStepSouthPath, true),
                Arguments.of("succeed flat west", threeByThreeCrossFlatWorld, singleFlatStepWestPath, true),
                Arguments.of("succeed flat east", threeByThreeCrossFlatWorld, singleFlatStepEastPath, true),
                Arguments.of("succeed flat northwest", threeByThreeOpenFlatWorld, diagonalFlatStepNorthWestPath, true),
                Arguments.of("succeed flat northeast", threeByThreeOpenFlatWorld, diagonalFlatStepNorthEastPath, true),
                Arguments.of("succeed flat southwest", threeByThreeOpenFlatWorld, diagonalFlatStepSouthWestPath, true),
                Arguments.of("succeed flat southeast", threeByThreeOpenFlatWorld, diagonalFlatStepSouthEastPath, true),
                Arguments.of("fail ascend north", threeByThreeCrossAscendingOutWorld, singleAscendingStepNorthPath, false),
                Arguments.of("fail ascend south", threeByThreeCrossAscendingOutWorld, singleAscendingStepSouthPath, false),
                Arguments.of("fail ascend west", threeByThreeCrossAscendingOutWorld, singleAscendingStepWestPath, false),
                Arguments.of("fail ascend east", threeByThreeCrossAscendingOutWorld, singleAscendingStepEastPath, false),
                Arguments.of("fail ascend early northwest", threeByThreeOpenAscendingFullOutWorld, diagonalAscendingStepNorthWestPath,
                        false),
                Arguments.of("fail ascend early northeast", threeByThreeOpenAscendingFullOutWorld, diagonalAscendingStepNorthEastPath,
                        false),
                Arguments.of("fail ascend early southwest", threeByThreeOpenAscendingFullOutWorld, diagonalAscendingStepSouthWestPath,
                        false),
                Arguments.of("fail ascend early southeast", threeByThreeOpenAscendingFullOutWorld, diagonalAscendingStepSouthEastPath,
                        false),
                Arguments.of("fail ascend late northwest", threeByThreeOpenAscendingCornerOutWorld, diagonalAscendingStepNorthWestPath,
                        false),
                Arguments.of("fail ascend late northeast", threeByThreeOpenAscendingCornerOutWorld, diagonalAscendingStepNorthEastPath,
                        false),
                Arguments.of("fail ascend late southwest", threeByThreeOpenAscendingCornerOutWorld, diagonalAscendingStepSouthWestPath,
                        false),
                Arguments.of("fail ascend late southeast", threeByThreeOpenAscendingCornerOutWorld, diagonalAscendingStepSouthEastPath,
                        false),
                Arguments.of("succeed descend north", threeByThreeCrossAscendingOutWorld, singleDescendingStepNorthPath, true),
                Arguments.of("succeed descend south", threeByThreeCrossAscendingOutWorld, singleDescendingStepSouthPath, true),
                Arguments.of("succeed descend west", threeByThreeCrossAscendingOutWorld, singleDescendingStepWestPath, true),
                Arguments.of("succeed descend east", threeByThreeCrossAscendingOutWorld, singleDescendingStepEastPath, true),
                Arguments.of("succeed descend early northwest", threeByThreeOpenAscendingCornerOutWorld,
                        diagonalDescendingStepNorthWestPath, true),
                Arguments.of("succeed descend early northeast", threeByThreeOpenAscendingCornerOutWorld,
                        diagonalDescendingStepNorthEastPath, true),
                Arguments.of("succeed descend early southwest", threeByThreeOpenAscendingCornerOutWorld,
                        diagonalDescendingStepSouthWestPath, true),
                Arguments.of("succeed descend early southeast", threeByThreeOpenAscendingCornerOutWorld,
                        diagonalDescendingStepSouthEastPath, true),
                Arguments.of("succeed descend late northwest", threeByThreeOpenAscendingFullOutWorld,
                        diagonalDescendingStepNorthWestPath, true),
                Arguments.of("succeed descend late northeast", threeByThreeOpenAscendingFullOutWorld,
                        diagonalDescendingStepNorthEastPath, true),
                Arguments.of("succeed descend late southwest", threeByThreeOpenAscendingFullOutWorld,
                        diagonalDescendingStepSouthWestPath, true),
                Arguments.of("succeed descend late southeast", threeByThreeOpenAscendingFullOutWorld,
                        diagonalDescendingStepSouthEastPath, true),
                Arguments.of(
                        "one time down",
                        new String[]{
                                "X |XX"
                        }, new String[]{
                                "! | ?"
                        },
                        true
                ),
                Arguments.of(
                        "two times down",
                        new String[]{
                                "X  |XX |XXX"
                        }, new String[]{
                                "!  | 1 |  ?"
                        },
                        true
                ),
                Arguments.of(
                        "diagonally late down",
                        new String[]{
                                "  |XX",
                                "X |XX"
                        }, new String[]{
                                "  | ?",
                                "! |  "
                        },
                        true
                ),
                Arguments.of(
                        "diagonally early down",
                        new String[]{
                                "X |XX",
                                "XX|XX"
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
                                "XXX|XXX|XXX",
                                "   |   |XXX",
                                "XXX|XXX|XXX",
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
                                "~  ",
                                "~  ",
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
                                "~  |~  ",
                                "~  |~  ",
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
                        true
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
                        true
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
                        true
                )
        );
    }

    public static Stream<Arguments> combinedMovementParameters() {
        return Stream.of(
                // TODO: Re-enable this test once the related FlexiblePathFinding test is re-enabled
                // (see org.terasology.flexiblepathfinding.WalkingLeapingFallingJPSTest)
//                Arguments.of(
//                        "up and down again",
//                        new String[]{
//                                "X    X|XX XX|XXXXX"
//                        }, new String[]{
//                                "?    !|     |     "
//                        },
//                        true,
//                        new String[]{"walking", "leaping", "falling"}
//                ),
                // TODO: Re-enable this test once the related FlexiblePathFinding test is re-enabled
                // (see org.terasology.flexiblepathfinding.WalkingLeapingFallingJPSTest)
//                Arguments.of(
//                        "down and up again",
//                        new String[]{
//                                "  X  | XXX |XXXXX"
//                        }, new String[]{
//                                "     |     |?   !"
//                        },
//                        true,
//                        new String[]{"walking", "leaping", "falling"}
//                ),
                Arguments.of(
                        "jump over",
                        new String[]{
                                "X X|XXX|XXX|XXX"
                        }, new String[]{
                                "? !|123|   |   "
                        },
                        true,
                        new String[]{"walking", "leaping", "falling"}
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
                        true,
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
                Arguments.of("succeed flat north", threeByThreeCrossFlatWorld, singleFlatStepNorthPath, true),
                Arguments.of("succeed flat south", threeByThreeCrossFlatWorld, singleFlatStepSouthPath, true),
                Arguments.of("succeed flat west", threeByThreeCrossFlatWorld, singleFlatStepWestPath, true),
                Arguments.of("succeed flat east", threeByThreeCrossFlatWorld, singleFlatStepEastPath, true),
                Arguments.of("succeed flat northwest", threeByThreeOpenFlatWorld, diagonalFlatStepNorthWestPath, true),
                Arguments.of("succeed flat northeast", threeByThreeOpenFlatWorld, diagonalFlatStepNorthEastPath, true),
                Arguments.of("succeed flat southwest", threeByThreeOpenFlatWorld, diagonalFlatStepSouthWestPath, true),
                Arguments.of("succeed flat southeast", threeByThreeOpenFlatWorld, diagonalFlatStepSouthEastPath, true),
                Arguments.of("succeed ascend north", threeByThreeCrossAscendingOutWorld, singleAscendingStepNorthPath, true),
                Arguments.of("succeed ascend south", threeByThreeCrossAscendingOutWorld, singleAscendingStepSouthPath, true),
                Arguments.of("succeed ascend west", threeByThreeCrossAscendingOutWorld, singleAscendingStepWestPath, true),
                Arguments.of("succeed ascend east", threeByThreeCrossAscendingOutWorld, singleAscendingStepEastPath, true),
                Arguments.of("succeed ascend early northwest", threeByThreeOpenAscendingFullOutWorld,
                        diagonalAscendingStepNorthWestPath, true),
                Arguments.of("succeed ascend early northeast", threeByThreeOpenAscendingFullOutWorld,
                        diagonalAscendingStepNorthEastPath, true),
                Arguments.of("succeed ascend early southwest", threeByThreeOpenAscendingFullOutWorld,
                        diagonalAscendingStepSouthWestPath, true),
                Arguments.of("succeed ascend early southeast", threeByThreeOpenAscendingFullOutWorld,
                        diagonalAscendingStepSouthEastPath, true),
                Arguments.of("succeed ascend late northwest", threeByThreeOpenAscendingCornerOutWorld,
                        diagonalAscendingStepNorthWestPath, true),
                Arguments.of("succeed ascend late northeast", threeByThreeOpenAscendingCornerOutWorld,
                        diagonalAscendingStepNorthEastPath, true),
                Arguments.of("succeed ascend late southwest", threeByThreeOpenAscendingCornerOutWorld,
                        diagonalAscendingStepSouthWestPath, true),
                Arguments.of("succeed ascend late southeast", threeByThreeOpenAscendingCornerOutWorld,
                        diagonalAscendingStepSouthEastPath, true),
                Arguments.of("succeed descend north", threeByThreeCrossAscendingOutWorld, singleDescendingStepNorthPath, true),
                Arguments.of("succeed descend south", threeByThreeCrossAscendingOutWorld, singleDescendingStepSouthPath, true),
                Arguments.of("succeed descend west", threeByThreeCrossAscendingOutWorld, singleDescendingStepWestPath, true),
                Arguments.of("succeed descend east", threeByThreeCrossAscendingOutWorld, singleDescendingStepEastPath, true),
                Arguments.of("succeed descend early northwest", threeByThreeOpenAscendingCornerOutWorld,
                        diagonalDescendingStepNorthWestPath, true),
                Arguments.of("succeed descend early northeast", threeByThreeOpenAscendingCornerOutWorld,
                        diagonalDescendingStepNorthEastPath, true),
                Arguments.of("succeed descend early southwest", threeByThreeOpenAscendingCornerOutWorld,
                        diagonalDescendingStepSouthWestPath, true),
                Arguments.of("succeed descend early southeast", threeByThreeOpenAscendingCornerOutWorld,
                        diagonalDescendingStepSouthEastPath, true),
                Arguments.of("succeed descend late northwest", threeByThreeOpenAscendingFullOutWorld,
                        diagonalDescendingStepNorthWestPath, true),
                Arguments.of("succeed descend late northeast", threeByThreeOpenAscendingFullOutWorld,
                        diagonalDescendingStepNorthEastPath, true),
                Arguments.of("succeed descend late southwest", threeByThreeOpenAscendingFullOutWorld,
                        diagonalDescendingStepSouthWestPath, true),
                Arguments.of("succeed descend late southeast", threeByThreeOpenAscendingFullOutWorld,
                        diagonalDescendingStepSouthEastPath, true),
                Arguments.of(
                        "one time up",
                        new String[]{
                                "X |XX"
                        }, new String[]{
                                "? | !"
                        },
                        true
                ),
                Arguments.of(
                        "one time down",
                        new String[]{
                                "X |XX"
                        }, new String[]{
                                "! | ?"
                        },
                        true
                ),
                Arguments.of(
                        "two times up",
                        new String[]{
                                "X  |XX |XXX"
                        }, new String[]{
                                "?  | 1 |  !"
                        },
                        true
                ),
                Arguments.of(
                        "two times down",
                        new String[]{
                                "X  |XX |XXX"
                        }, new String[]{
                                "!  | 1 |  ?"
                        },
                        true
                ),
                Arguments.of(
                        "diagonally early up",
                        new String[]{
                                "  |XX",
                                "X |XX"
                        }, new String[]{
                                "  | !",
                                "? |  "
                        },
                        true
                ),
                Arguments.of(
                        "diagonally late up",
                        new String[]{
                                "X |XX",
                                "XX|XX"
                        }, new String[]{
                                "  | !",
                                "? |  "
                        },
                        true
                ),
                Arguments.of(
                        "diagonally late down",
                        new String[]{
                                "  |XX",
                                "X |XX"
                        }, new String[]{
                                "  | ?",
                                "! |  "
                        },
                        true
                ),
                Arguments.of(
                        "diagonally early down",
                        new String[]{
                                "X |XX",
                                "XX|XX"
                        }, new String[]{
                                "  | ?",
                                "! |  "
                        },
                        true
                ),
                Arguments.of(
                        "gap",
                        new String[]{
                                " X |XXX"
                        }, new String[]{
                                "   |? !"
                        },
                        true
                )
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
    void testNonFunctionalFallingMovement(String name, String[]world, String[]path, boolean successExpected) {
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
            logger.info("pos: {}", pos);
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

    @AfterEach
    void cleanUp () {
        chunkProvider.purgeWorld();
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
