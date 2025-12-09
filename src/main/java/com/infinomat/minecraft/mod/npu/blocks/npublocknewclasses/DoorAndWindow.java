package com.infinomat.minecraft.mod.npu.blocks.npublocknewclasses;

import com.infinomat.minecraft.mod.npu.blocks.NpuBlocks;
import com.infinomat.minecraft.mod.npu.blocks.npublocknewclasses.common.LoadShape;
import com.infinomat.minecraft.mod.npu.util.register.data.template.BlockShapeData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DoorAndWindow extends HorizontalDirectionalBlock implements LoadShape {
    // 额外属性
    private static final MapCodec<DoorAndWindow> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    propertiesCodec(),
                    Codec.STRING.fieldOf("load_method").forGetter(p -> p.loadMethod.name()),
                    Codec.STRING.fieldOf("direction").forGetter(p -> p.direction.name()),
                    Codec.BOOL.fieldOf("is_open").forGetter(p -> p.open)
            ).apply(instance, DoorAndWindow::new)
    );
    protected final NpuBlocks.LoadMethod loadMethod;
    protected static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    // 存储形状
    protected VoxelShape shape;
    // 当前开关状态
    protected boolean open;
    // 当前方向
    protected Direction direction;
    // 存储方块的形状
    protected final List<VoxelShape> shapeList_open;
    protected final List<VoxelShape> shapeList_close;
    @Override
    protected @NotNull MapCodec<? extends DoorAndWindow> codec() {
        return CODEC;
    }

    // 构造
    private DoorAndWindow(Properties properties, List<VoxelShape> shapeList_open, List<VoxelShape> shapeList_close, NpuBlocks.LoadMethod loadMethod) {
        super(properties);
        this.loadMethod = loadMethod;
        this.shapeList_open = shapeList_open;
        this.shapeList_close = shapeList_close;
        this.open = false;
    }
    private DoorAndWindow(Properties properties, String loadMethod, String direction, Boolean open) {
        super(properties);
        this.loadMethod = NpuBlocks.LoadMethod.valueOf(loadMethod);
        this.direction = Direction.valueOf(direction);
        this.shapeList_open = new ArrayList<>();
        this.shapeList_close = new ArrayList<>();
        this.open = open;
    }
    public static Supplier<DoorAndWindow> Factory(Properties properties, BlockShapeData shapeData_open, BlockShapeData shapeData_close, NpuBlocks.LoadMethod loadMethod) {
        final List<VoxelShape> shapeList_open = new ArrayList<>(6);
        final List<VoxelShape> shapeList_close = new ArrayList<>(6);
        if (!shapeData_open.loaderIsObj()) for (List<Double> shape : shapeData_open.getShapeList()) {
            shapeList_open.add(Shapes.box(shape.get(0), shape.get(1), shape.get(2), shape.get(3), shape.get(4), shape.get(5)));
        }
        if (!shapeData_close.loaderIsObj()) for (List<Double> shape : shapeData_close.getShapeList()) {
            shapeList_close.add(Shapes.box(shape.get(0), shape.get(1), shape.get(2), shape.get(3), shape.get(4), shape.get(5)));
        }
        return () -> new DoorAndWindow(properties, shapeList_open, shapeList_close, loadMethod);
    }

    // 额外属性注册
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OPEN);
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }

    // 放置时状态
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(OPEN, false).setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    // 设置形状
    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pGetter, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        if (shape == null || direction != pState.getValue(FACING) || open != pState.getValue(OPEN)) {
            direction = pState.getValue(FACING);
            open = pState.getValue(OPEN);
            shape = loadShape(open ? shapeList_open : shapeList_close, loadMethod, pState.getValue(FACING));
        }
        return shape;
    }

    // 被空手点击时
    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                                        @NotNull Player player, @NotNull BlockHitResult res) {
        state = state.cycle(OPEN);
        level.setBlock(pos, state, 10);
        level.gameEvent(player, state.getValue(OPEN) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
        return InteractionResult.SUCCESS;
    }
}
