package com.infinomat.minecraft.mod.npu.blocks.npublocknewclasses;

import com.infinomat.minecraft.mod.npu.blocks.NpuBlocks;
import com.infinomat.minecraft.mod.npu.blocks.npublocknewclasses.common.Common;
import com.infinomat.minecraft.mod.npu.blocks.npublocknewclasses.common.LoadShape;
import com.infinomat.minecraft.mod.npu.util.register.data.template.BlockShapeData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class NormalStructure extends Block implements LoadShape, Common {
    // 额外属性
    private static final MapCodec<NormalStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    propertiesCodec(),
                    Codec.STRING.fieldOf("load_method").forGetter(p -> p.loadMethod.name())
            ).apply(instance, NormalStructure::new)
    );
    protected final NpuBlocks.LoadMethod loadMethod;
    // 体积
    protected final List<VoxelShape> shapeList;
    protected VoxelShape shape;
    @Override
    protected @NotNull MapCodec<? extends NormalStructure> codec() {
        return CODEC;
    }

    // 构造
    private NormalStructure(BlockBehaviour.Properties properties, List<VoxelShape> shapeList, NpuBlocks.LoadMethod loadMethod) {
        super(properties);
        this.shapeList = shapeList;
        shape = null;
        this.loadMethod = loadMethod;
    }
    private NormalStructure(BlockBehaviour.Properties properties, String loadMethod) {
        this(properties, new ArrayList<>(), NpuBlocks.LoadMethod.valueOf(loadMethod));
    }

    public static Supplier<NormalStructure> Factory(BlockBehaviour.Properties properties, BlockShapeData shapeData, NpuBlocks.LoadMethod loadMethod){
        ArrayList<VoxelShape> shapeList = new ArrayList<>();
        if (!shapeData.loaderIsObj()) for (List<Double> shape : shapeData.getShapeList()) {
            shapeList.add(Shapes.box(shape.get(0), shape.get(1), shape.get(2), shape.get(3), shape.get(4), shape.get(5)));
        }
        return () -> new NormalStructure(properties, shapeList, loadMethod);
    }

    // 设置形状
    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pGetter, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        if (shape == null) {
            shape = loadShape(shapeList, loadMethod);
        }
        return shape.optimize();
    }
}
