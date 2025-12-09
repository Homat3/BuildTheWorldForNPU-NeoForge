package com.infinomat.minecraft.mod.npu.items;

import com.infinomat.minecraft.mod.npu.blocks.NpuBlocks;
import com.infinomat.minecraft.mod.npu.entities.NpuEntities;
import com.infinomat.minecraft.mod.npu.items.npuitemnewclasses.VehicleItem;
import com.infinomat.minecraft.mod.npu.util.FolderDataGetter;
import com.infinomat.minecraft.mod.npu.util.PathTools;
import com.infinomat.minecraft.mod.npu.util.Reference;
import com.infinomat.minecraft.mod.npu.util.register.data.RegisterList;
import com.infinomat.minecraft.mod.npu.util.register.data.template.ItemTemplate;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NpuItems {
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final String templateFolder = "template";
    public static final String registerFoder = "register";
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Reference.MOD_ID);
    public static final String baseFolderPath = PathTools.linkPath(Reference.PATH.get(Reference.PathType.LOADER), Reference.PATH.get(Reference.PathType.ITEM));
    public static final HashMap<String, RegisterList> RegisterListMap = new HashMap<>();// 待注册物品列表
    private static final HashMap<String, ItemRegister> TemplateMap = new HashMap<>();   //  模版映射
    public static final HashMap<String, List<DeferredItem<Item>>> CreativeModeTabItemMap = new HashMap<>();    // 物品栏-物品映射
    public static final HashMap<String, List<DeferredItem<BlockItem>>> CreativeModeTabBlockItemMap = new HashMap<>();    // 物品栏-物品映射

    static {
        loadRegisterList();
        loadTemplate();
        register();
    }

    public static List<DeferredItem<Item>> getItemList(String tabName) {
        return CreativeModeTabItemMap.get(tabName);
    }
    public static List<DeferredItem<BlockItem>> getBlockItemList(String tabName) {
        return CreativeModeTabBlockItemMap.get(tabName);
    }

    private static void loadRegisterList() {
        String folderPath = PathTools.linkPath(baseFolderPath, registerFoder);

        for (RegisterList registerList : new FolderDataGetter<>(folderPath, RegisterList.class).getList()) {
            RegisterListMap.put(registerList.getId(), registerList);
        }
    }

    private static void loadTemplate() {
        String folderPath = PathTools.linkPath(baseFolderPath, templateFolder);

        for (ItemTemplate itemTemplate : new FolderDataGetter<>(folderPath, ItemTemplate.class).getList()) {
            TemplateMap.put(itemTemplate.getId(), ItemRegister.create(itemTemplate));
        }
    }

    private static void register() {
        for (var creativeModeTab : RegisterListMap.keySet()) {
            CreativeModeTabItemMap.put(creativeModeTab, new ArrayList<>());
            CreativeModeTabBlockItemMap.put(creativeModeTab, new ArrayList<>());
            for (var group : RegisterListMap.get(creativeModeTab).getGroups()) {
                CreativeModeTabItemMap.get(creativeModeTab).addAll(TemplateMap.get(group.template).register(group.items));
            }
        }
        for (var creativeModeTab : NpuBlocks.CreativeModeTabMap.keySet()) {
            CreativeModeTabItemMap.computeIfAbsent(creativeModeTab, k -> new ArrayList<>());
            CreativeModeTabBlockItemMap.computeIfAbsent(creativeModeTab, k -> new ArrayList<>());
            NpuBlocks.CreativeModeTabMap.get(creativeModeTab).forEach(block -> CreativeModeTabBlockItemMap.get(creativeModeTab)
                    .add(ITEMS.register(
                            NpuBlocks.BlockIdMap.get(block),
                            () -> new BlockItem(block.get(), NpuBlocks.createBlockItemProperties(block))
                    )));
        }
    }

    private record ItemRegister(ItemTemplate template) {
        public static ItemRegister create(ItemTemplate template_data) {
            return new ItemRegister(template_data);
        }

        public List<DeferredItem<Item>> register(List<String> ids) {
            ArrayList<DeferredItem<Item>> itemList = new ArrayList<>();
            ids.forEach(id -> {
                DeferredItem<Item> ITEM;
                if (template.isSpawnEgg())
                    ITEM = ITEMS.register(id, () ->
                            new SpawnEggItem(NpuEntities.MOB_ID_MAP.get(id).get(), createItemProperties(id)));
                else if (template.isVehicle())
                    ITEM = ITEMS.register(id, () ->
                            new VehicleItem(NpuEntities.VEHICLE_ID_MAP.get(id).get(), createItemProperties(id)));
                else
                    ITEM = ITEMS.register(id, () -> new Item(createItemProperties(id)));

                itemList.add(ITEM);
                LOGGER.info("Registered item: {}", ITEM.getId());
            });
            return itemList;
        }

        private static Item.Properties createItemProperties(String id) {

            Item.Properties properties = new Item.Properties();

            ResourceLocation LOCATION = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, id);
            return properties.setId(ResourceKey.create(ResourceKey.createRegistryKey(LOCATION), LOCATION));
        }
    }
}
