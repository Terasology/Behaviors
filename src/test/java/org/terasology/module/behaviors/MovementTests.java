// Copyright 2022 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.module.behaviors;

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
import org.terasology.engine.world.chunks.ChunkProvider;
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

    private static final float defaultCharHeight = 0.9f;
    private static final float defaultCharRadius = 0.3f;

    private static final String[] defaultMovementModes = { "walking", "leaping", "falling" };

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

    public static Stream<Arguments> walkingMovementParameters() {
        return Stream.of(
                Arguments.of("succeed flat north", twoBlockVerticalFlatWorld, singleFlatStepNorthPath, true),
                Arguments.of("succeed flat south", twoBlockVerticalFlatWorld, singleFlatStepSouthPath, true),
                Arguments.of("succeed flat west", twoBlockHorizontalFlatWorld, singleFlatStepWestPath, true),
                Arguments.of("succeed flat east", twoBlockHorizontalFlatWorld, singleFlatStepEastPath, true),
                Arguments.of("succeed flat northwest", twoTimesTwoBlockFlatWorld, diagonalFlatStepNorthWestPath, true),
                Arguments.of("succeed flat northeast", twoTimesTwoBlockFlatWorld, diagonalFlatStepNorthEastPath, true),
                Arguments.of("succeed flat southwest", twoTimesTwoBlockFlatWorld, diagonalFlatStepSouthWestPath, true),
                Arguments.of("succeed flat southeast", twoTimesTwoBlockFlatWorld, diagonalFlatStepSouthEastPath, true),
                Arguments.of("fail ascend north", twoBlockVerticalAscendingNorthWorld, singleAscendingStepNorthPath, false),
                Arguments.of("fail ascend south", twoBlockVerticalDescendingNorthWorld, singleAscendingStepSouthPath, false),
                Arguments.of("fail ascend west", twoBlockHorizontalAscendingWestWorld, singleAscendingStepWestPath, false),
                Arguments.of("fail ascend east", twoBlockHorizontalDescendingWestWorld, singleAscendingStepEastPath, false),
                Arguments.of("fail ascend early northwest", twoTimesTwoBlockAscendingNorthWestWorld, diagonalAscendingStepNorthWestPath, false),
                Arguments.of("fail ascend early northeast", twoTimesTwoBlockAscendingNorthEastWorld, diagonalAscendingStepNorthEastPath, false),
                Arguments.of("fail ascend early southwest", twoTimesTwoBlockAscendingSouthWestWorld, diagonalAscendingStepSouthWestPath, false),
                Arguments.of("fail ascend early southeast", twoTimesTwoBlockAscendingSouthEastWorld, diagonalAscendingStepSouthEastPath, false),
                Arguments.of("fail ascend late northwest", twoTimesTwoBlockDescendingSouthEastWorld, diagonalAscendingStepNorthWestPath, false),
                Arguments.of("fail ascend late northeast", twoTimesTwoBlockDescendingSouthWestWorld, diagonalAscendingStepNorthEastPath, false),
                Arguments.of("fail ascend late southwest", twoTimesTwoBlockDescendingNorthEastWorld, diagonalAscendingStepSouthWestPath, false),
                Arguments.of("fail ascend late southeast", twoTimesTwoBlockDescendingNorthWestWorld, diagonalAscendingStepSouthEastPath, false),
                Arguments.of("fail descend north", twoBlockVerticalAscendingNorthWorld, singleDescendingStepNorthPath, false),
                Arguments.of("fail descend south", twoBlockVerticalDescendingNorthWorld, singleDescendingStepSouthPath, false),
                Arguments.of("fail descend west", twoBlockHorizontalAscendingWestWorld, singleDescendingStepWestPath, false),
                Arguments.of("fail descend east", twoBlockHorizontalDescendingWestWorld, singleDescendingStepEastPath, false),
                Arguments.of("fail descend early northwest", twoTimesTwoBlockAscendingNorthWestWorld, diagonalDescendingStepNorthWestPath, false),
                Arguments.of("fail descend early northeast", twoTimesTwoBlockAscendingNorthEastWorld, diagonalDescendingStepNorthEastPath, false),
                Arguments.of("fail descend early southwest", twoTimesTwoBlockAscendingSouthWestWorld, diagonalDescendingStepSouthWestPath, false),
                Arguments.of("fail descend early southeast", twoTimesTwoBlockAscendingSouthEastWorld, diagonalDescendingStepSouthEastPath, false),
                Arguments.of("fail descend late northwest", twoTimesTwoBlockDescendingSouthEastWorld, diagonalDescendingStepNorthWestPath, false),
                Arguments.of("fail descend late northeast", twoTimesTwoBlockDescendingSouthWestWorld, diagonalDescendingStepNorthEastPath, false),
                Arguments.of("fail descend late southwest", twoTimesTwoBlockDescendingNorthEastWorld, diagonalDescendingStepSouthWestPath, false),
                Arguments.of("fail descend late southeast", twoTimesTwoBlockDescendingNorthWestWorld, diagonalDescendingStepSouthEastPath, false),
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
                        false
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
                        false
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
                        false
                ),
                Arguments.of(
                        "gap",
                        new String[]{
                                " X |XXX"
                        }, new String[]{
                                "   |? !"
                        },
                        false
                )
        );
    }

    // Leaping movements are purely vertical (up) and require additional horizontal walking movements
    // The following test cases only attempt to test that the leaping plugin alone is not incorrectly allowing basic movements
    public static Stream<Arguments> nonFunctionalLeapingMovementParameters() {
        return Stream.of(
                Arguments.of("fail flat north", twoBlockVerticalFlatWorld, singleFlatStepNorthPath, false),
                Arguments.of("fail flat south", twoBlockVerticalFlatWorld, singleFlatStepSouthPath, false),
                Arguments.of("fail flat west", twoBlockHorizontalFlatWorld, singleFlatStepWestPath, false),
                Arguments.of("fail flat east", twoBlockHorizontalFlatWorld, singleFlatStepEastPath, false),
                Arguments.of("fail flat northwest", twoTimesTwoBlockFlatWorld, diagonalFlatStepNorthWestPath, false),
                Arguments.of("fail flat northeast", twoTimesTwoBlockFlatWorld, diagonalFlatStepNorthEastPath, false),
                Arguments.of("fail flat southwest", twoTimesTwoBlockFlatWorld, diagonalFlatStepSouthWestPath, false),
                Arguments.of("fail flat southeast", twoTimesTwoBlockFlatWorld, diagonalFlatStepSouthEastPath, false),
                Arguments.of("fail ascend north", twoBlockVerticalAscendingNorthWorld, singleAscendingStepNorthPath, false),
                Arguments.of("fail ascend south", twoBlockVerticalDescendingNorthWorld, singleAscendingStepSouthPath, false),
                Arguments.of("fail ascend west", twoBlockHorizontalAscendingWestWorld, singleAscendingStepWestPath, false),
                Arguments.of("fail ascend east", twoBlockHorizontalDescendingWestWorld, singleAscendingStepEastPath, false),
                Arguments.of("fail ascend early northwest", twoTimesTwoBlockAscendingNorthWestWorld, diagonalAscendingStepNorthWestPath, false),
                Arguments.of("fail ascend early northeast", twoTimesTwoBlockAscendingNorthEastWorld, diagonalAscendingStepNorthEastPath, false),
                Arguments.of("fail ascend early southwest", twoTimesTwoBlockAscendingSouthWestWorld, diagonalAscendingStepSouthWestPath, false),
                Arguments.of("fail ascend early southeast", twoTimesTwoBlockAscendingSouthEastWorld, diagonalAscendingStepSouthEastPath, false),
                Arguments.of("fail ascend late northwest", twoTimesTwoBlockDescendingSouthEastWorld, diagonalAscendingStepNorthWestPath, false),
                Arguments.of("fail ascend late northeast", twoTimesTwoBlockDescendingSouthWestWorld, diagonalAscendingStepNorthEastPath, false),
                Arguments.of("fail ascend late southwest", twoTimesTwoBlockDescendingNorthEastWorld, diagonalAscendingStepSouthWestPath, false),
                Arguments.of("fail ascend late southeast", twoTimesTwoBlockDescendingNorthWestWorld, diagonalAscendingStepSouthEastPath, false),
                Arguments.of("fail descend north", twoBlockVerticalAscendingNorthWorld, singleDescendingStepNorthPath, false),
                Arguments.of("fail descend south", twoBlockVerticalDescendingNorthWorld, singleDescendingStepSouthPath, false),
                Arguments.of("fail descend west", twoBlockHorizontalAscendingWestWorld, singleDescendingStepWestPath, false),
                Arguments.of("fail descend east", twoBlockHorizontalDescendingWestWorld, singleDescendingStepEastPath, false),
                Arguments.of("fail descend early northwest", twoTimesTwoBlockAscendingNorthWestWorld, diagonalDescendingStepNorthWestPath, false),
                Arguments.of("fail descend early northeast", twoTimesTwoBlockAscendingNorthEastWorld, diagonalDescendingStepNorthEastPath, false),
                Arguments.of("fail descend early southwest", twoTimesTwoBlockAscendingSouthWestWorld, diagonalDescendingStepSouthWestPath, false),
                Arguments.of("fail descend early southeast", twoTimesTwoBlockAscendingSouthEastWorld, diagonalDescendingStepSouthEastPath, false),
                Arguments.of("fail descend late northwest", twoTimesTwoBlockDescendingSouthEastWorld, diagonalDescendingStepNorthWestPath, false),
                Arguments.of("fail descend late northeast", twoTimesTwoBlockDescendingSouthWestWorld, diagonalDescendingStepNorthEastPath, false),
                Arguments.of("fail descend late southwest", twoTimesTwoBlockDescendingNorthEastWorld, diagonalDescendingStepSouthWestPath, false),
                Arguments.of("fail descend late southeast", twoTimesTwoBlockDescendingNorthWestWorld, diagonalDescendingStepSouthEastPath, false)
        );
    }

    // Leaping movements are purely vertical (up) and require additional horizontal walking movements
    // The following test cases attempt to verify that the leaping in combination with the walking plugin correctly allows expected
    // movements
    public static Stream<Arguments> leapingMovementParameters() {
        return Stream.of(
                Arguments.of("succeed flat north", twoBlockVerticalFlatWorld, singleFlatStepNorthPath, true),
                Arguments.of("succeed flat south", twoBlockVerticalFlatWorld, singleFlatStepSouthPath, true),
                Arguments.of("succeed flat west", twoBlockHorizontalFlatWorld, singleFlatStepWestPath, true),
                Arguments.of("succeed flat east", twoBlockHorizontalFlatWorld, singleFlatStepEastPath, true),
                Arguments.of("succeed flat northwest", twoTimesTwoBlockFlatWorld, diagonalFlatStepNorthWestPath, true),
                Arguments.of("succeed flat northeast", twoTimesTwoBlockFlatWorld, diagonalFlatStepNorthEastPath, true),
                Arguments.of("succeed flat southwest", twoTimesTwoBlockFlatWorld, diagonalFlatStepSouthWestPath, true),
                Arguments.of("succeed flat southeast", twoTimesTwoBlockFlatWorld, diagonalFlatStepSouthEastPath, true),
                Arguments.of("succeed ascend north", twoBlockVerticalAscendingNorthWorld, singleAscendingStepNorthPath, true),
                Arguments.of("succeed ascend south", twoBlockVerticalDescendingNorthWorld, singleAscendingStepSouthPath, true),
                Arguments.of("succeed ascend west", twoBlockHorizontalAscendingWestWorld, singleAscendingStepWestPath, true),
                Arguments.of("succeed ascend east", twoBlockHorizontalDescendingWestWorld, singleAscendingStepEastPath, true),
                Arguments.of("succeed ascend early northwest", twoTimesTwoBlockAscendingNorthWestWorld, diagonalAscendingStepNorthWestPath, true),
                Arguments.of("succeed ascend early northeast", twoTimesTwoBlockAscendingNorthEastWorld, diagonalAscendingStepNorthEastPath, true),
                Arguments.of("succeed ascend early southwest", twoTimesTwoBlockAscendingSouthWestWorld, diagonalAscendingStepSouthWestPath, true),
                Arguments.of("succeed ascend early southeast", twoTimesTwoBlockAscendingSouthEastWorld, diagonalAscendingStepSouthEastPath, true),
                Arguments.of("succeed ascend late northwest", twoTimesTwoBlockDescendingSouthEastWorld, diagonalAscendingStepNorthWestPath, true),
                Arguments.of("succeed ascend late northeast", twoTimesTwoBlockDescendingSouthWestWorld, diagonalAscendingStepNorthEastPath, true),
                Arguments.of("succeed ascend late southwest", twoTimesTwoBlockDescendingNorthEastWorld, diagonalAscendingStepSouthWestPath, true),
                Arguments.of("succeed ascend late southeast", twoTimesTwoBlockDescendingNorthWestWorld, diagonalAscendingStepSouthEastPath, true),
                Arguments.of("fail descend north", twoBlockVerticalAscendingNorthWorld, singleDescendingStepNorthPath, false),
                Arguments.of("fail descend south", twoBlockVerticalDescendingNorthWorld, singleDescendingStepSouthPath, false),
                Arguments.of("fail descend west", twoBlockHorizontalAscendingWestWorld, singleDescendingStepWestPath, false),
                Arguments.of("fail descend east", twoBlockHorizontalDescendingWestWorld, singleDescendingStepEastPath, false),
                Arguments.of("fail descend early northwest", twoTimesTwoBlockAscendingNorthWestWorld, diagonalDescendingStepNorthWestPath, false),
                Arguments.of("fail descend early northeast", twoTimesTwoBlockAscendingNorthEastWorld, diagonalDescendingStepNorthEastPath, false),
                Arguments.of("fail descend early southwest", twoTimesTwoBlockAscendingSouthWestWorld, diagonalDescendingStepSouthWestPath, false),
                Arguments.of("fail descend early southeast", twoTimesTwoBlockAscendingSouthEastWorld, diagonalDescendingStepSouthEastPath, false),
                Arguments.of("fail descend late northwest", twoTimesTwoBlockDescendingSouthEastWorld, diagonalDescendingStepNorthWestPath, false),
                Arguments.of("fail descend late northeast", twoTimesTwoBlockDescendingSouthWestWorld, diagonalDescendingStepNorthEastPath, false),
                Arguments.of("fail descend late southwest", twoTimesTwoBlockDescendingNorthEastWorld, diagonalDescendingStepSouthWestPath, false),
                Arguments.of("fail descend late southeast", twoTimesTwoBlockDescendingNorthWestWorld, diagonalDescendingStepSouthEastPath, false),
                Arguments.of(
                        "one time up",
                        new String[]{
                                "X |XX"
                        }, new String[]{
                                "? | !"
                        },
                        false
                ),
                Arguments.of(
                        "two times up",
                        new String[]{
                                "X  |XX |XXX"
                        }, new String[]{
                                "?  | 1 |  !"
                        },
                        false
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
                        false
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
                        false
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
                        false
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
                        false
                )
        );
    }

    // Falling movements are purely vertical (down) and require additional horizontal walking movements
    // The following test cases only attempt to verify that the falling plugin alone is not incorrectly allowing basic movements
    public static Stream<Arguments> nonFunctionalFallingMovementParameters() {
        return Stream.of(
                Arguments.of("fail flat north", twoBlockVerticalFlatWorld, singleFlatStepNorthPath, false),
                Arguments.of("fail flat south", twoBlockVerticalFlatWorld, singleFlatStepSouthPath, false),
                Arguments.of("fail flat west", twoBlockHorizontalFlatWorld, singleFlatStepWestPath, false),
                Arguments.of("fail flat east", twoBlockHorizontalFlatWorld, singleFlatStepEastPath, false),
                Arguments.of("fail flat northwest", twoTimesTwoBlockFlatWorld, diagonalFlatStepNorthWestPath, false),
                Arguments.of("fail flat northeast", twoTimesTwoBlockFlatWorld, diagonalFlatStepNorthEastPath, false),
                Arguments.of("fail flat southwest", twoTimesTwoBlockFlatWorld, diagonalFlatStepSouthWestPath, false),
                Arguments.of("fail flat southeast", twoTimesTwoBlockFlatWorld, diagonalFlatStepSouthEastPath, false),
                Arguments.of("fail ascend north", twoBlockVerticalAscendingNorthWorld, singleAscendingStepNorthPath, false),
                Arguments.of("fail ascend south", twoBlockVerticalDescendingNorthWorld, singleAscendingStepSouthPath, false),
                Arguments.of("fail ascend west", twoBlockHorizontalAscendingWestWorld, singleAscendingStepWestPath, false),
                Arguments.of("fail ascend east", twoBlockHorizontalDescendingWestWorld, singleAscendingStepEastPath, false),
                Arguments.of("fail ascend early northwest", twoTimesTwoBlockAscendingNorthWestWorld, diagonalAscendingStepNorthWestPath, false),
                Arguments.of("fail ascend early northeast", twoTimesTwoBlockAscendingNorthEastWorld, diagonalAscendingStepNorthEastPath, false),
                Arguments.of("fail ascend early southwest", twoTimesTwoBlockAscendingSouthWestWorld, diagonalAscendingStepSouthWestPath, false),
                Arguments.of("fail ascend early southeast", twoTimesTwoBlockAscendingSouthEastWorld, diagonalAscendingStepSouthEastPath, false),
                Arguments.of("fail ascend late northwest", twoTimesTwoBlockDescendingSouthEastWorld, diagonalAscendingStepNorthWestPath, false),
                Arguments.of("fail ascend late northeast", twoTimesTwoBlockDescendingSouthWestWorld, diagonalAscendingStepNorthEastPath, false),
                Arguments.of("fail ascend late southwest", twoTimesTwoBlockDescendingNorthEastWorld, diagonalAscendingStepSouthWestPath, false),
                Arguments.of("fail ascend late southeast", twoTimesTwoBlockDescendingNorthWestWorld, diagonalAscendingStepSouthEastPath, false),
                Arguments.of("fail descend north", twoBlockVerticalAscendingNorthWorld, singleDescendingStepNorthPath, false),
                Arguments.of("fail descend south", twoBlockVerticalDescendingNorthWorld, singleDescendingStepSouthPath, false),
                Arguments.of("fail descend west", twoBlockHorizontalAscendingWestWorld, singleDescendingStepWestPath, false),
                Arguments.of("fail descend east", twoBlockHorizontalDescendingWestWorld, singleDescendingStepEastPath, false),
                Arguments.of("fail descend early northwest", twoTimesTwoBlockAscendingNorthWestWorld, diagonalDescendingStepNorthWestPath, false),
                Arguments.of("fail descend early northeast", twoTimesTwoBlockAscendingNorthEastWorld, diagonalDescendingStepNorthEastPath, false),
                Arguments.of("fail descend early southwest", twoTimesTwoBlockAscendingSouthWestWorld, diagonalDescendingStepSouthWestPath, false),
                Arguments.of("fail descend early southeast", twoTimesTwoBlockAscendingSouthEastWorld, diagonalDescendingStepSouthEastPath, false),
                Arguments.of("fail descend late northwest", twoTimesTwoBlockDescendingSouthEastWorld, diagonalDescendingStepNorthWestPath, false),
                Arguments.of("fail descend late northeast", twoTimesTwoBlockDescendingSouthWestWorld, diagonalDescendingStepNorthEastPath, false),
                Arguments.of("fail descend late southwest", twoTimesTwoBlockDescendingNorthEastWorld, diagonalDescendingStepSouthWestPath, false),
                Arguments.of("fail descend late southeast", twoTimesTwoBlockDescendingNorthWestWorld, diagonalDescendingStepSouthEastPath, false)
        );
    }

    // Falling movements are purely vertical (down) and require additional horizontal walking movements
    // The following test cases attempt to verify that the falling in combination with the walking plugin correctly allows expected
    // movements
    public static Stream<Arguments> fallingMovementParameters() {
        return Stream.of(
                Arguments.of("succeed flat north", twoBlockVerticalFlatWorld, singleFlatStepNorthPath, true),
                Arguments.of("succeed flat south", twoBlockVerticalFlatWorld, singleFlatStepSouthPath, true),
                Arguments.of("succeed flat west", twoBlockHorizontalFlatWorld, singleFlatStepWestPath, true),
                Arguments.of("succeed flat east", twoBlockHorizontalFlatWorld, singleFlatStepEastPath, true),
                Arguments.of("succeed flat northwest", twoTimesTwoBlockFlatWorld, diagonalFlatStepNorthWestPath, true),
                Arguments.of("succeed flat northeast", twoTimesTwoBlockFlatWorld, diagonalFlatStepNorthEastPath, true),
                Arguments.of("succeed flat southwest", twoTimesTwoBlockFlatWorld, diagonalFlatStepSouthWestPath, true),
                Arguments.of("succeed flat southeast", twoTimesTwoBlockFlatWorld, diagonalFlatStepSouthEastPath, true),
                Arguments.of("fail ascend north", twoBlockVerticalAscendingNorthWorld, singleAscendingStepNorthPath, false),
                Arguments.of("fail ascend south", twoBlockVerticalDescendingNorthWorld, singleAscendingStepSouthPath, false),
                Arguments.of("fail ascend west", twoBlockHorizontalAscendingWestWorld, singleAscendingStepWestPath, false),
                Arguments.of("fail ascend east", twoBlockHorizontalDescendingWestWorld, singleAscendingStepEastPath, false),
                Arguments.of("fail ascend early northwest", twoTimesTwoBlockAscendingNorthWestWorld, diagonalAscendingStepNorthWestPath, false),
                Arguments.of("fail ascend early northeast", twoTimesTwoBlockAscendingNorthEastWorld, diagonalAscendingStepNorthEastPath, false),
                Arguments.of("fail ascend early southwest", twoTimesTwoBlockAscendingSouthWestWorld, diagonalAscendingStepSouthWestPath, false),
                Arguments.of("fail ascend early southeast", twoTimesTwoBlockAscendingSouthEastWorld, diagonalAscendingStepSouthEastPath, false),
                Arguments.of("fail ascend late northwest", twoTimesTwoBlockDescendingSouthEastWorld, diagonalAscendingStepNorthWestPath, false),
                Arguments.of("fail ascend late northeast", twoTimesTwoBlockDescendingSouthWestWorld, diagonalAscendingStepNorthEastPath, false),
                Arguments.of("fail ascend late southwest", twoTimesTwoBlockDescendingNorthEastWorld, diagonalAscendingStepSouthWestPath, false),
                Arguments.of("fail ascend late southeast", twoTimesTwoBlockDescendingNorthWestWorld, diagonalAscendingStepSouthEastPath, false),
                Arguments.of("succeed descend north", twoBlockVerticalAscendingNorthWorld, singleDescendingStepNorthPath, true),
                Arguments.of("succeed descend south", twoBlockVerticalDescendingNorthWorld, singleDescendingStepSouthPath, true),
                Arguments.of("succeed descend west", twoBlockHorizontalAscendingWestWorld, singleDescendingStepWestPath, true),
                Arguments.of("succeed descend east", twoBlockHorizontalDescendingWestWorld, singleDescendingStepEastPath, true),
                Arguments.of("succeed descend early northwest", twoTimesTwoBlockAscendingNorthWestWorld, diagonalDescendingStepNorthWestPath, true),
                Arguments.of("succeed descend early northeast", twoTimesTwoBlockAscendingNorthEastWorld, diagonalDescendingStepNorthEastPath, true),
                Arguments.of("succeed descend early southwest", twoTimesTwoBlockAscendingSouthWestWorld, diagonalDescendingStepSouthWestPath, true),
                Arguments.of("succeed descend early southeast", twoTimesTwoBlockAscendingSouthEastWorld, diagonalDescendingStepSouthEastPath, true),
                Arguments.of("succeed descend late northwest", twoTimesTwoBlockDescendingSouthEastWorld, diagonalDescendingStepNorthWestPath, true),
                Arguments.of("succeed descend late northeast", twoTimesTwoBlockDescendingSouthWestWorld, diagonalDescendingStepNorthEastPath, true),
                Arguments.of("succeed descend late southwest", twoTimesTwoBlockDescendingNorthEastWorld, diagonalDescendingStepSouthWestPath, true),
                Arguments.of("succeed descend late southeast", twoTimesTwoBlockDescendingNorthWestWorld, diagonalDescendingStepSouthEastPath, true),
                Arguments.of(
                        "one time down",
                        new String[]{
                                "X |XX"
                        }, new String[]{
                                "! | ?"
                        },
                        false
                ),
                Arguments.of(
                        "two times down",
                        new String[]{
                                "X  |XX |XXX"
                        }, new String[]{
                                "!  | 1 |  ?"
                        },
                        false
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
                        false
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
                        false
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
                        false
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
                        false
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
                        false
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
                        false
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
                        false
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
                        false
                )
        );
    }

    public static Stream<Arguments> combinedMovementParameters() {
        return Stream.of(
                Arguments.of(
                        "up and down again",
                        new String[]{
                                "X    X|XX XX|XXXXX"
                        }, new String[]{
                                "?    !|     |     "
                        },
                        false,
                        new String[]{"walking", "leaping", "falling"}
                ),
                Arguments.of(
                        "jump over",
                        new String[]{
                                "X X|XXX|XXX|XXX"
                        }, new String[]{
                                "? !|123|   |   "
                        },
                        false,
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
                        false,
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
                        false,
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
                Arguments.of("succeed flat north", twoBlockVerticalFlatWorld, singleFlatStepNorthPath, true),
                Arguments.of("succeed flat south", twoBlockVerticalFlatWorld, singleFlatStepSouthPath, true),
                Arguments.of("succeed flat west", twoBlockHorizontalFlatWorld, singleFlatStepWestPath, true),
                Arguments.of("succeed flat east", twoBlockHorizontalFlatWorld, singleFlatStepEastPath, true),
                Arguments.of("succeed flat northwest", twoTimesTwoBlockFlatWorld, diagonalFlatStepNorthWestPath, true),
                Arguments.of("succeed flat northeast", twoTimesTwoBlockFlatWorld, diagonalFlatStepNorthEastPath, true),
                Arguments.of("succeed flat southwest", twoTimesTwoBlockFlatWorld, diagonalFlatStepSouthWestPath, true),
                Arguments.of("succeed flat southeast", twoTimesTwoBlockFlatWorld, diagonalFlatStepSouthEastPath, true),
                Arguments.of("succeed ascend north", twoBlockVerticalAscendingNorthWorld, singleAscendingStepNorthPath, true),
                Arguments.of("succeed ascend south", twoBlockVerticalDescendingNorthWorld, singleAscendingStepSouthPath, true),
                Arguments.of("succeed ascend west", twoBlockHorizontalAscendingWestWorld, singleAscendingStepWestPath, true),
                Arguments.of("succeed ascend east", twoBlockHorizontalDescendingWestWorld, singleAscendingStepEastPath, true),
                Arguments.of("succeed ascend early northwest", twoTimesTwoBlockAscendingNorthWestWorld, diagonalAscendingStepNorthWestPath, true),
                Arguments.of("succeed ascend early northeast", twoTimesTwoBlockAscendingNorthEastWorld, diagonalAscendingStepNorthEastPath, true),
                Arguments.of("succeed ascend early southwest", twoTimesTwoBlockAscendingSouthWestWorld, diagonalAscendingStepSouthWestPath, true),
                Arguments.of("succeed ascend early southeast", twoTimesTwoBlockAscendingSouthEastWorld, diagonalAscendingStepSouthEastPath, true),
                Arguments.of("succeed ascend late northwest", twoTimesTwoBlockDescendingSouthEastWorld, diagonalAscendingStepNorthWestPath, true),
                Arguments.of("succeed ascend late northeast", twoTimesTwoBlockDescendingSouthWestWorld, diagonalAscendingStepNorthEastPath, true),
                Arguments.of("succeed ascend late southwest", twoTimesTwoBlockDescendingNorthEastWorld, diagonalAscendingStepSouthWestPath, true),
                Arguments.of("succeed ascend late southeast", twoTimesTwoBlockDescendingNorthWestWorld, diagonalAscendingStepSouthEastPath, true),
                Arguments.of("succeed descend north", twoBlockVerticalAscendingNorthWorld, singleDescendingStepNorthPath, true),
                Arguments.of("succeed descend south", twoBlockVerticalDescendingNorthWorld, singleDescendingStepSouthPath, true),
                Arguments.of("succeed descend west", twoBlockHorizontalAscendingWestWorld, singleDescendingStepWestPath, true),
                Arguments.of("succeed descend east", twoBlockHorizontalDescendingWestWorld, singleDescendingStepEastPath, true),
                Arguments.of("succeed descend early northwest", twoTimesTwoBlockAscendingNorthWestWorld, diagonalDescendingStepNorthWestPath, true),
                Arguments.of("succeed descend early northeast", twoTimesTwoBlockAscendingNorthEastWorld, diagonalDescendingStepNorthEastPath, true),
                Arguments.of("succeed descend early southwest", twoTimesTwoBlockAscendingSouthWestWorld, diagonalDescendingStepSouthWestPath, true),
                Arguments.of("succeed descend early southeast", twoTimesTwoBlockAscendingSouthEastWorld, diagonalDescendingStepSouthEastPath, true),
                Arguments.of("succeed descend late northwest", twoTimesTwoBlockDescendingSouthEastWorld, diagonalDescendingStepNorthWestPath, true),
                Arguments.of("succeed descend late northeast", twoTimesTwoBlockDescendingSouthWestWorld, diagonalDescendingStepNorthEastPath, true),
                Arguments.of("succeed descend late southwest", twoTimesTwoBlockDescendingNorthEastWorld, diagonalDescendingStepSouthWestPath, true),
                Arguments.of("succeed descend late southeast", twoTimesTwoBlockDescendingNorthWestWorld, diagonalDescendingStepSouthEastPath, true),
                Arguments.of(
                        "one time up",
                        new String[]{
                                "X |XX"
                        }, new String[]{
                                "? | !"
                        },
                        false
                ),
                Arguments.of(
                        "one time down",
                        new String[]{
                                "X |XX"
                        }, new String[]{
                                "! | ?"
                        },
                        false
                ),
                Arguments.of(
                        "two times up",
                        new String[]{
                                "X  |XX |XXX"
                        }, new String[]{
                                "?  | 1 |  !"
                        },
                        false
                ),
                Arguments.of(
                        "two times down",
                        new String[]{
                                "X  |XX |XXX"
                        }, new String[]{
                                "!  | 1 |  ?"
                        },
                        false
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
                        false
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
                        false
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
                        false
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
                        false
                ),
                Arguments.of(
                        "gap",
                        new String[]{
                                " X |XXX"
                        }, new String[]{
                                "   |? !"
                        },
                        false
                )
        );
    }

    @MethodSource("walkingMovementParameters")
    @ParameterizedTest(name = "walking: {0}")
    @DisplayName("Test movement plugin for walking")
    void testWalkingMovement(String name, String[] world, String[] path, boolean successExpected) {
        int airHeight = 41;

        setupWorld(world, airHeight);

        // find start and goal positions from path data
        Vector3i start = new Vector3i();
        Vector3i stop = new Vector3i();
        detectPath(path, airHeight, start, stop);

        EntityRef entity = createMovingCharacter(defaultCharHeight, defaultCharRadius, start, stop, "walking");

        helper.runUntil(() -> Blocks.toBlockPos(entity.getComponent(LocationComponent.class)
                .getWorldPosition(new Vector3f())).distance(start) <= 0.5F);

        boolean timedOut = helper.runWhile(() -> {
            Vector3f pos = entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f());
            logger.info("pos: {}", pos);
            return Blocks.toBlockPos(pos).distance(stop) > 0;
        });

        if (successExpected) {
            Assertions.assertFalse(timedOut, () -> String.format("Test character (at %s) cannot reach destination point (at %s)",
                    Blocks.toBlockPos(entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f())),
                    stop
            ));
        } else {
            Assertions.assertTrue(timedOut, () -> String.format("Test character (at %s) cannot reach destination point (at %s)",
                    Blocks.toBlockPos(entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f())),
                    stop
            ));
        }

        entity.destroy();
    }

    @MethodSource("nonFunctionalLeapingMovementParameters")
    @ParameterizedTest(name = "leaping: {0}")
    @DisplayName("Test movement plugin for leaping (intentionally without walking)")
    void testNonFunctionalLeapingMovement(String name, String[] world, String[] path, boolean successExpected) {
        int airHeight = 41;

        setupWorld(world, airHeight);

        // find start and goal positions from path data
        Vector3i start = new Vector3i();
        Vector3i stop = new Vector3i();
        detectPath(path, airHeight, start, stop);

        EntityRef entity = createMovingCharacter(defaultCharHeight, defaultCharRadius, start, stop, "leaping");

        helper.runUntil(() -> Blocks.toBlockPos(entity.getComponent(LocationComponent.class)
                .getWorldPosition(new Vector3f())).distance(start) <= 0.5F);

        boolean timedOut = helper.runWhile(() -> {
            Vector3f pos = entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f());
            logger.info("pos: {}", pos);
            return Blocks.toBlockPos(pos).distance(stop) > 0;
        });

        if (successExpected) {
            Assertions.assertFalse(timedOut, () -> String.format("Test character (at %s) cannot reach destination point (at %s)",
                    Blocks.toBlockPos(entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f())),
                    stop
            ));
        } else {
            Assertions.assertTrue(timedOut, () -> String.format("Test character (at %s) cannot reach destination point (at %s)",
                    Blocks.toBlockPos(entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f())),
                    stop
            ));
        }

        entity.destroy();
    }

    @MethodSource("leapingMovementParameters")
    @ParameterizedTest(name = "walking, leaping: {0}")
    @DisplayName("Test movement plugin for leaping (requires walking)")
    void testLeapingMovement(String name, String[] world, String[] path, boolean successExpected) {
        int airHeight = 41;

        setupWorld(world, airHeight);

        // find start and goal positions from path data
        Vector3i start = new Vector3i();
        Vector3i stop = new Vector3i();
        detectPath(path, airHeight, start, stop);

        EntityRef entity = createMovingCharacter(defaultCharHeight, defaultCharRadius, start, stop, "walking", "leaping");

        helper.runUntil(() -> Blocks.toBlockPos(entity.getComponent(LocationComponent.class)
                .getWorldPosition(new Vector3f())).distance(start) <= 0.5F);

        boolean timedOut = helper.runWhile(() -> {
            Vector3f pos = entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f());
            logger.info("pos: {}", pos);
            return Blocks.toBlockPos(pos).distance(stop) > 0;
        });

        if (successExpected) {
            Assertions.assertFalse(timedOut, () -> String.format("Test character (at %s) cannot reach destination point (at %s)",
                    Blocks.toBlockPos(entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f())),
                    stop
            ));
        } else {
            Assertions.assertTrue(timedOut, () -> String.format("Test character (at %s) cannot reach destination point (at %s)",
                    Blocks.toBlockPos(entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f())),
                    stop
            ));
        }

        entity.destroy();
    }

    @MethodSource("nonFunctionalFallingMovementParameters")
    @ParameterizedTest(name = "falling: {0}")
    @DisplayName("Test movement plugin for falling (intentionally without walking)")
    void testNonFunctionalFallingMovement(String name, String[]world, String[]path, boolean successExpected) {
        int airHeight = 41;

        setupWorld(world, airHeight);

        // find start and goal positions from path data
        Vector3i start = new Vector3i();
        Vector3i stop = new Vector3i();
        detectPath(path, airHeight, start, stop);

        EntityRef entity = createMovingCharacter(defaultCharHeight, defaultCharRadius, start, stop, "falling");

        helper.runUntil(() -> Blocks.toBlockPos(entity.getComponent(LocationComponent.class)
                .getWorldPosition(new Vector3f())).distance(start) <= 0.5F);

        boolean timedOut = helper.runWhile(() -> {
            Vector3f pos = entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f());
            logger.info("pos: {}", pos);
            return Blocks.toBlockPos(pos).distance(stop) > 0;
        });

        if (successExpected) {
            Assertions.assertFalse(timedOut, () -> String.format("Test character (at %s) cannot reach destination point (at %s)",
                    Blocks.toBlockPos(entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f())),
                    stop
            ));
        } else {
            Assertions.assertTrue(timedOut, () -> String.format("Test character (at %s) cannot reach destination point (at %s)",
                    Blocks.toBlockPos(entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f())),
                    stop
            ));
        }

        entity.destroy();
    }

    @MethodSource("fallingMovementParameters")
    @ParameterizedTest(name = "walking, falling: {0}")
    @DisplayName("Test movement plugin for falling (requires walking)")
    void testFallingMovement(String name, String[]world, String[]path, boolean successExpected) {
        int airHeight = 41;

        setupWorld(world, airHeight);

        // find start and goal positions from path data
        Vector3i start = new Vector3i();
        Vector3i stop = new Vector3i();
        detectPath(path, airHeight, start, stop);

        EntityRef entity = createMovingCharacter(defaultCharHeight, defaultCharRadius, start, stop, "walking", "falling");

        helper.runUntil(() -> Blocks.toBlockPos(entity.getComponent(LocationComponent.class)
                .getWorldPosition(new Vector3f())).distance(start) <= 0.5F);

        boolean timedOut = helper.runWhile(() -> {
            Vector3f pos = entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f());
            logger.info("pos: {}", pos);
            return Blocks.toBlockPos(pos).distance(stop) > 0;
        });

        if (successExpected) {
            Assertions.assertFalse(timedOut, () -> String.format("Test character (at %s) cannot reach destination point (at %s)",
                    Blocks.toBlockPos(entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f())),
                    stop
            ));
        } else {
            Assertions.assertTrue(timedOut, () -> String.format("Test character (at %s) cannot reach destination point (at %s)",
                    Blocks.toBlockPos(entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f())),
                    stop
            ));
        }

        entity.destroy();
    }

    @MethodSource("flyingMovementParameters")
    @ParameterizedTest(name = "flying: {0}")
    @DisplayName("Test movement plugin for flying")
    void testFlyingMovement(String name, String[]world, String[]path, boolean successExpected) {
        int airHeight = 41;

        setupWorld(world, airHeight);

        // find start and goal positions from path data
        Vector3i start = new Vector3i();
        Vector3i stop = new Vector3i();
        detectPath(path, airHeight, start, stop);

        EntityRef entity = createMovingCharacter(defaultCharHeight, defaultCharRadius, start, stop, "flying");

        helper.runUntil(() -> Blocks.toBlockPos(entity.getComponent(LocationComponent.class)
                .getWorldPosition(new Vector3f())).distance(start) <= 0.5F);

        boolean timedOut = helper.runWhile(() -> {
            Vector3f pos = entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f());
            logger.info("pos: {}", pos);
            return Blocks.toBlockPos(pos).distance(stop) > 0;
        });

        if (successExpected) {
            Assertions.assertFalse(timedOut, () -> String.format("Test character (at %s) cannot reach destination point (at %s)",
                    Blocks.toBlockPos(entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f())),
                    stop
            ));
        } else {
            Assertions.assertTrue(timedOut, () -> String.format("Test character (at %s) cannot reach destination point (at %s)",
                    Blocks.toBlockPos(entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f())),
                    stop
            ));
        }

        entity.destroy();
    }

    @MethodSource("swimmingMovementParameters")
    @ParameterizedTest(name = "swimming: {0}")
    @DisplayName("Test movement plugin for swimming")
    void testSwimmingMovement(String name, String[]world, String[]path, boolean successExpected) {
        int airHeight = 41;

        setupWorld(world, airHeight);

        // find start and goal positions from path data
        Vector3i start = new Vector3i();
        Vector3i stop = new Vector3i();
        detectPath(path, airHeight, start, stop);

        EntityRef entity = createMovingCharacter(defaultCharHeight, defaultCharRadius, start, stop, "swimming");

        helper.runUntil(() -> Blocks.toBlockPos(entity.getComponent(LocationComponent.class)
                .getWorldPosition(new Vector3f())).distance(start) <= 0.5F);

        boolean timedOut = helper.runWhile(() -> {
            Vector3f pos = entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f());
            logger.info("pos: {}", pos);
            return Blocks.toBlockPos(pos).distance(stop) > 0;
        });

        if (successExpected) {
            Assertions.assertFalse(timedOut, () -> String.format("Test character (at %s) cannot reach destination point (at %s)",
                    Blocks.toBlockPos(entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f())),
                    stop
            ));
        } else {
            Assertions.assertTrue(timedOut, () -> String.format("Test character (at %s) cannot reach destination point (at %s)",
                    Blocks.toBlockPos(entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f())),
                    stop
            ));
        }

        entity.destroy();
    }

    @MethodSource("combinedMovementParameters")
    @ParameterizedTest(name = "{6}: {0}")
    @DisplayName("Test movement plugin combinations")
    void testCombinedMovement(String name, String[]world, String[]path, boolean successExpected, String...movementTypes) {
        int airHeight = 41;

        setupWorld(world, airHeight);

        // find start and goal positions from path data
        Vector3i start = new Vector3i();
        Vector3i stop = new Vector3i();
        detectPath(path, airHeight, start, stop);

        EntityRef entity = createMovingCharacter(defaultCharHeight, defaultCharRadius, start, stop, movementTypes);

        helper.runUntil(() -> Blocks.toBlockPos(entity.getComponent(LocationComponent.class)
                .getWorldPosition(new Vector3f())).distance(start) <= 0.5F);

        boolean timedOut = helper.runWhile(() -> {
            Vector3f pos = entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f());
            logger.info("pos: {}", pos);
            return Blocks.toBlockPos(pos).distance(stop) > 0;
        });

        if (successExpected) {
            Assertions.assertFalse(timedOut, () -> String.format("Test character (at %s) cannot reach destination point (at %s)",
                    Blocks.toBlockPos(entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f())),
                    stop
            ));
        } else {
            Assertions.assertTrue(timedOut, () -> String.format("Test character (at %s) cannot reach destination point (at %s)",
                    Blocks.toBlockPos(entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f())),
                    stop
            ));
        }

        entity.destroy();
    }

    @MethodSource("defaultPluginCombinationParameters")
    @ParameterizedTest(name = "default: {0}")
    @DisplayName("Test default movement plugin combinations for comparison")
    void testDefaultMovement(String name, String[]world, String[]path, boolean successExpected) {
        int airHeight = 41;

        setupWorld(world, airHeight);

        // find start and goal positions from path data
        Vector3i start = new Vector3i();
        Vector3i stop = new Vector3i();
        detectPath(path, airHeight, start, stop);

        logger.info("Default movement plugin combination: {}", defaultMovementModes);

        EntityRef entity = createMovingCharacter(defaultCharHeight, defaultCharRadius, start, stop, defaultMovementModes);

        helper.runUntil(() -> Blocks.toBlockPos(entity.getComponent(LocationComponent.class)
                .getWorldPosition(new Vector3f())).distance(start) <= 0.5F);

        boolean timedOut = helper.runWhile(() -> {
            Vector3f pos = entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f());
            logger.info("pos: {}", pos);
            return Blocks.toBlockPos(pos).distance(stop) > 0;
        });

        if (successExpected) {
            Assertions.assertFalse(timedOut, () -> String.format("Test character (at %s) cannot reach destination point (at %s)",
                    Blocks.toBlockPos(entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f())),
                    stop
            ));
        } else {
            Assertions.assertTrue(timedOut, () -> String.format("Test character (at %s) cannot reach destination point (at %s)",
                    Blocks.toBlockPos(entity.getComponent(LocationComponent.class).getWorldPosition(new Vector3f())),
                    stop
            ));
        }

        entity.destroy();
    }

    @AfterEach
    void cleanUp () {
        chunkProvider.purgeWorld();
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

    private void setupWorld (String[]world,int airHeight){
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

    private BlockRegionc getPaddedExtents (String[]world,int airHeight){
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
