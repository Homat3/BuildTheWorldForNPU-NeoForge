package com.infinomat.minecraft.mod.npu.blocks.npublocknewclasses;

import com.infinomat.minecraft.mod.npu.blocks.NpuBlocks;
import com.infinomat.minecraft.mod.npu.blocks.npublocknewclasses.common.LoadShape;
import com.infinomat.minecraft.mod.npu.util.register.data.template.BlockShapeData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class HorizontalMultipleDirectionalStructure extends Block implements LoadShape {
    // 额外属性
    private static final MapCodec<HorizontalMultipleDirectionalStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    propertiesCodec(),
                    Codec.STRING.fieldOf("load_method").forGetter(p -> p.loadMethod.name()),
                    Codec.INT.fieldOf("angle").forGetter(p -> p.angle)
            ).apply(instance, HorizontalMultipleDirectionalStructure::new)
    );
    protected static final IntegerProperty ANGEL = IntegerProperty.create("angle", 0, 11);
    protected final NpuBlocks.LoadMethod loadMethod;
    protected VoxelShape shape;
    protected int angle;
    // 体积映射
    private final List<List<VoxelShape>> angleShapeList;


    @Override
    protected @NotNull MapCodec<? extends HorizontalMultipleDirectionalStructure> codec() {
        return CODEC;
    }

    // 构造
    public HorizontalMultipleDirectionalStructure(Properties properties, List<List<VoxelShape>> shapeList, NpuBlocks.LoadMethod loadMethod) {
        super(properties);
        this.angleShapeList = shapeList;
        this.shape = null;
        this.loadMethod = loadMethod;
        this.angle = 0;
    }
    private HorizontalMultipleDirectionalStructure(Properties properties, String loadMethod, int angle) {
        super(properties);
        this.angleShapeList = new ArrayList<>(0);
        this.shape = null;
        this.loadMethod = NpuBlocks.LoadMethod.valueOf(loadMethod);
        this.angle = angle;
    }

    public static Supplier<HorizontalMultipleDirectionalStructure> Factory(Properties properties, ArrayList<BlockShapeData> shapeDatas, NpuBlocks.LoadMethod loadMethod) {
        final List<List<VoxelShape>> shapeLists = new ArrayList<>(6);
        shapeDatas.forEach(shapeData -> {
            if (!shapeData.loaderIsObj()) for (List<Double> shape : shapeData.getShapeList()) {
                shapeLists.get(shapeDatas.indexOf(shapeData))
                        .add(Shapes.box(shape.get(0), shape.get(1), shape.get(2), shape.get(3), shape.get(4), shape.get(5)));
            }
        });
        return () -> new HorizontalMultipleDirectionalStructure(properties, shapeLists, loadMethod);
    }

    // 额外属性注册
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ANGEL);
    }

    // 放置时状态
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(ANGEL, switch (context.getHorizontalDirection().getOpposite()) {
            case WEST, EAST -> 6;
            default -> 0;
        });
    }

    // 设置形状
    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pGetter, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        if (shape == null || angle != pState.getValue(ANGEL)) {
            angle = pState.getValue(ANGEL);
            shape = loadShape(angleShapeList.get(angle % 6), loadMethod);
        }
        return shape.optimize();
    }

    // 被空手点击时
    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                                        @NotNull Player player, @NotNull BlockHitResult res) {
        state = state.setValue(ANGEL, (state.getValue(ANGEL) + 1) % 12);
        level.setBlock(pos, state, 10);
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        return InteractionResult.SUCCESS;
    }
}
