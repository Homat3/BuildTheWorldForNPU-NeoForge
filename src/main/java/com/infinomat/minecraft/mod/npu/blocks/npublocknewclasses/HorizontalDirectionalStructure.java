package com.infinomat.minecraft.mod.npu.blocks.npublocknewclasses;

import com.infinomat.minecraft.mod.npu.blocks.NpuBlocks;
import com.infinomat.minecraft.mod.npu.blocks.npublocknewclasses.common.LoadShape;
import com.infinomat.minecraft.mod.npu.util.register.data.template.BlockShapeData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class HorizontalDirectionalStructure extends HorizontalDirectionalBlock implements LoadShape {
    // 额外属性
    private static final MapCodec<HorizontalDirectionalStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    propertiesCodec(),
                    Codec.STRING.fieldOf("load_method").forGetter(p -> p.loadMethod.name()),
                    Codec.STRING.fieldOf("direction").forGetter(p -> p.direction.name())
            ).apply(instance, HorizontalDirectionalStructure::new)
    );
    protected final NpuBlocks.LoadMethod loadMethod;
    // 存储方块的形状
    protected final List<VoxelShape> shapeList;
    protected VoxelShape shape;
    // 当前方向
    protected Direction direction;


    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    // 构造
    private HorizontalDirectionalStructure(BlockBehaviour.Properties properties, List<VoxelShape> shapeList, NpuBlocks.LoadMethod loadMethod) {
        super(properties);
        this.shape = null;
        this.shapeList = shapeList;
        this.direction = Direction.NORTH;
        this.loadMethod = loadMethod;
    }
    private HorizontalDirectionalStructure(BlockBehaviour.Properties properties, String loadMethod, String direction) {
        super(properties);
        this.shape = null;
        this.shapeList = new ArrayList<>();
        this.direction = Direction.valueOf(direction);
        this.loadMethod = NpuBlocks.LoadMethod.valueOf(loadMethod);
    }
    public static Supplier<HorizontalDirectionalStructure> Factory(Properties properties, BlockShapeData shapeData, NpuBlocks.LoadMethod loadMethod) {
        ArrayList<VoxelShape> shapeList = new ArrayList<>(0);
        if (!shapeData.loaderIsObj()) for (List<Double> shape : shapeData.getShapeList()) {
            shapeList.add(Shapes.box(shape.get(0), shape.get(1), shape.get(2), shape.get(3), shape.get(4), shape.get(5)));
        }
        return () -> new HorizontalDirectionalStructure(properties, shapeList, loadMethod);
    }

    // 额外属性注册
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    // 放置时状态
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    // 设置形状
    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pGetter, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        if (shape == null || direction != pState.getValue(FACING)) {
            direction = pState.getValue(FACING);
            shape = loadShape(shapeList, loadMethod, direction);
        }
        return shape;
    }
}
