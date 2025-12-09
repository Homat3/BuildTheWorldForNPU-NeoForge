package com.infinomat.minecraft.mod.npu.blocks.npublocknewclasses.common;

import com.infinomat.minecraft.mod.npu.blocks.NpuBlocks;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public interface LoadShape {
    default VoxelShape loadShape(List<VoxelShape> shapeList, NpuBlocks.LoadMethod loadMethod){
        return loadShape(shapeList, loadMethod, null);
    }
    default VoxelShape loadShape(List<VoxelShape> shapeList, NpuBlocks.LoadMethod loadMethod, Direction direction) {
        VoxelShape shape = Shapes.empty();
        if (!shapeList.isEmpty()) switch (loadMethod) {
            case METICULOUS:
                for (VoxelShape voxelShape : shapeList) {
                    if (shape.isEmpty()){
                        shape = getShapeByDirection(voxelShape, direction);
                    }
                    else {
                        shape = Shapes.or(shape, getShapeByDirection(voxelShape, direction));
                    }
                }
                break;
            case ROUGH:
                for (VoxelShape voxelShape : shapeList) {
                    if (shape.isEmpty()){
                        shape = voxelShape;
                    }
                    else {
                        AABB a = shape.bounds();
                        AABB b = voxelShape.bounds();
                        shape = Shapes.box(
                                Math.min(a.minX, b.minX), Math.min(a.minY, b.minY), Math.min(a.minZ, b.minZ),
                                Math.max(a.maxX, b.maxX), Math.max(a.maxY, b.maxY), Math.max(a.maxZ, b.maxZ)
                        );
                    }
                }
                shape = getShapeByDirection(shape, direction);
                break;
        }
        return shape;
    }

    default VoxelShape getShapeByDirection(VoxelShape shape, Direction direction) {
        if (direction == null) return shape;
        Double[] pos = new Double[6];
        {
            AABB box = shape.bounds();
            pos[0] = box.minX;
            pos[1] = box.minY;
            pos[2] = box.minZ;
            pos[3] = box.maxX;
            pos[4] = box.maxY;
            pos[5] = box.maxZ;
        }

        return switch (direction) {
            //坐标变化表
            case NORTH -> Shapes.box(pos[0], pos[1], pos[2], pos[3], pos[4], pos[5]);
            case SOUTH -> Shapes.box(1 - pos[3], pos[1], 1 - pos[5], 1 - pos[0], pos[4], 1 - pos[2]);
            case EAST -> Shapes.box(1 - pos[5], pos[1], pos[0], 1 - pos[2], pos[4], pos[3]);
            case WEST -> Shapes.box(pos[2], pos[1], 1 - pos[3], pos[5], pos[4], 1 - pos[0]);
            default -> shape;
        };
    }
}
