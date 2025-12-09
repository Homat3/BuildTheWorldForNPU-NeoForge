package com.infinomat.minecraft.mod.npu.blocks;

import com.infinomat.minecraft.mod.npu.blocks.npublocknewclasses.*;
import com.infinomat.minecraft.mod.npu.blocks.npublocknewclasses.common.Common;
import com.infinomat.minecraft.mod.npu.util.FileDataGetter;
import com.infinomat.minecraft.mod.npu.util.FolderDataGetter;
import com.infinomat.minecraft.mod.npu.util.PathTools;
import com.infinomat.minecraft.mod.npu.util.Reference;
import com.infinomat.minecraft.mod.npu.util.register.data.RegisterList;
import com.infinomat.minecraft.mod.npu.util.register.data.template.BlockShapeData;
import com.infinomat.minecraft.mod.npu.util.register.data.template.BlockTemplate;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.ToIntFunction;

public class NpuBlocks {
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final String templateFolder = "template";
    public static final String registerFoder = "register";
    public static final String baseFolderPath = PathTools.linkPath(Reference.PATH.get(Reference.PathType.LOADER), Reference.PATH.get(Reference.PathType.BLOCK));
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Reference.MOD_ID);

    public static final HashMap<String, RegisterList> RegisterListMap = new HashMap<>();    // 待注册方块分类映射表
    public static final HashMap<DeferredBlock<Block>, String> BlockIdMap = new HashMap<>(); // 方块id表
    private static final HashMap<String, BlockRegister> TemplateMap = new HashMap<>();      // 注册模板表
    public static final HashMap<String, List<DeferredBlock<Block>>> CreativeModeTabMap = new HashMap<>();    // 物品栏-方块映射

    public static final List<DeferredBlock<Block>> blocks = new ArrayList<>();   // 方块表

    static {
        loadRegisterList();
        loadTemplate();
        register();
    }
    private static void loadRegisterList() {
        LOGGER.info("Loading Block Register List...");
        String folderPath = PathTools.linkPath(baseFolderPath, registerFoder);

        for (RegisterList registerList : new FolderDataGetter<>(folderPath, RegisterList.class).getList()) {
            RegisterListMap.put(registerList.getId(), registerList);
        }
    }
    private static void loadTemplate() {
        LOGGER.info("Loading Block Template...");
        String folderPath = PathTools.linkPath(baseFolderPath, templateFolder);

        for (BlockTemplate blockTemplate : new FolderDataGetter<>(folderPath, BlockTemplate.class).getList()) {
            TemplateMap.put(blockTemplate.getId(), BlockRegister.create(blockTemplate));
        }
    }

    private static void register() {
        for (var itemGroup : RegisterListMap.keySet()){
            CreativeModeTabMap.put(itemGroup, new ArrayList<>());
            for (var group: RegisterListMap.get(itemGroup).getGroups()){
                CreativeModeTabMap.get(itemGroup).addAll(TemplateMap.get(group.template).register(group.items));
            }
        }
    }

    public static Item.Properties createBlockItemProperties(DeferredBlock<Block> BLOCK) {
        Item.Properties properties = new Item.Properties();
        BLOCK.getId();
        return properties.setId(ResourceKey.create(ResourceKey.createRegistryKey(BLOCK.getId()), BLOCK.getId()));
    }

    public enum StructureType {
        NORMAL_STRUCTURE,
        HORIZONTAL_DIRECTIONAL_STRUCTURE,
        HORIZONTAL_MULTIPLE_DIRECTIONAL_STRUCTURE,
        NORMAL_HALF_SLAB,
        HORIZONTAL_DIRECTIONAL_HALF_SLAB,
        DOOR_AND_WINDOW
    }

    public enum Material {
        //EXAMPLE("example", 硬度, 音效包, (BlockState state) ->{根据不同的blockstate返回不同的亮度值}, 阻力系数，即站在上面的移速),
        IRON("iron", 5.0F, SoundType.METAL, (BlockState state) -> 0, 0.6F),
        ROCK("rock", 2.5F, SoundType.STONE, (BlockState state) -> 0, 0.6F);


        private final String name;
        private final float strength;
        private final SoundType sound;
        private final ToIntFunction<BlockState> lightLevel;
        private final float friction;

        Material(String name, float strength, SoundType sound, ToIntFunction<BlockState> lightLevel, float friction) {
            this.name = name;
            this.strength = strength;
            this.sound = sound;
            this.lightLevel = lightLevel;
            this.friction = friction;
        }

        public BlockBehaviour.Properties addToProperties(BlockBehaviour.Properties properties) {
            return properties.strength(strength).sound(sound).lightLevel(lightLevel).friction(friction);
        }

        public String getName() {
            return this.name;
        }
    }

    public enum EmunShape {
        FULL_SHPAE(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D),
        NULL_SHPAE(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D),
        HALF_SHPAE_BOTTOM(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D),
        HALF_SHPAE_TOP(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D);

        final VoxelShape shape;

        EmunShape(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
            shape = Shapes.box(minX, minY, minZ, maxX, maxY, maxZ);
        }

        public VoxelShape getShape() {
            return shape;
        }
    }

    public enum LoadMethod {
        METICULOUS, ROUGH
    }

    //一些常用属性
    private record BlockRegister(BlockTemplate template) {
        public static BlockRegister create(BlockTemplate template) {
            return new BlockRegister(template);
        }

        public List<DeferredBlock<Block>> register(List<String> ids) {
            List<DeferredBlock<Block>> blockList = new ArrayList<>();
            ids.forEach(id -> {
                DeferredBlock<Block> BLOCK;

                BLOCK = switch (StructureType.valueOf(template.StructureType)) {
                    case NORMAL_STRUCTURE -> {
                        BlockShapeData shapeData =
                                new FileDataGetter<>(template.getModelPath(id), BlockShapeData.class).getData();
                        yield BLOCKS.register(id, NormalStructure.Factory(Common.createBlockPropertiesOfMaterial(template, id), shapeData, LoadMethod.valueOf(template.loadMethod)));
                    }
                    case HORIZONTAL_DIRECTIONAL_STRUCTURE -> {
                        BlockShapeData shapeData =
                                new FileDataGetter<>(template.getModelPath(id), BlockShapeData.class).getData();
                        yield BLOCKS.register(id, HorizontalDirectionalStructure.Factory(Common.createBlockPropertiesOfMaterial(template, id), shapeData, LoadMethod.valueOf(template.loadMethod)));
                    }
                    case HORIZONTAL_MULTIPLE_DIRECTIONAL_STRUCTURE -> {
                        ArrayList<BlockShapeData> shapeDatas = new ArrayList<>(0);
                        for (int i = 0; i < 6; i++) {
                            shapeDatas.add(new FileDataGetter<>(template.getModelPath(id, String.valueOf(i)), BlockShapeData.class).getData());
                        }
                        yield BLOCKS.register(id, HorizontalMultipleDirectionalStructure.Factory(Common.createBlockPropertiesOfMaterial(template, id), shapeDatas, LoadMethod.valueOf(template.loadMethod))
                        );
                    }
                    case NORMAL_HALF_SLAB -> BLOCKS.register(id, NormalHalfSlab.Factory(Common.createBlockPropertiesOfMaterial(template, id), template.double_enable));
                    case HORIZONTAL_DIRECTIONAL_HALF_SLAB -> BLOCKS.register(id, HorizontalDirectionalHalfSlab.Factory(Common.createBlockPropertiesOfMaterial(template, id), template.double_enable));
                    case DOOR_AND_WINDOW -> {
                        BlockShapeData shapeData_open =
                                new FileDataGetter<>(template.getModelPath(id, "open"), BlockShapeData.class).getData();
                        BlockShapeData shapeData_close =
                                new FileDataGetter<>(template.getModelPath(id, "close"), BlockShapeData.class).getData();
                        yield BLOCKS.register(id, DoorAndWindow.Factory(Common.createBlockPropertiesOfMaterial(template, id), shapeData_open, shapeData_close, LoadMethod.valueOf(template.loadMethod)));
                    }
                };

                blockList.add(BLOCK);
                BlockIdMap.put(BLOCK, id);
                LOGGER.info("Registered Block: {}", BLOCK.getId());
            });
            return blockList;
        }
    }
}
