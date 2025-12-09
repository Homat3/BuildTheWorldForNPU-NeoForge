package com.infinomat.minecraft.mod.npu.blocks.npublocknewclasses.common;

import net.minecraft.core.Direction;

public interface DirectionCheck {
    static Direction checkAndReturn(Direction direction, DirectionRange range) {
        return switch (range) {
            case NO_VERTICAL -> switch (direction){
                case UP -> Direction.NORTH;
                case DOWN -> Direction.SOUTH;
                default -> direction;
            };
            case NO_HORIZONTAL -> switch (direction){
                case NORTH, SOUTH -> Direction.UP;
                case EAST, WEST -> Direction.SOUTH;
                default -> direction;
            };
            case FULL ->  direction;
            default -> null;
        };
    }

    enum DirectionRange {
        NO_VERTICAL,
        NO_HORIZONTAL,
        FULL
    }
}
