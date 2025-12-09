package com.infinomat.minecraft.mod.npu.creativemodtab;

import com.infinomat.minecraft.mod.npu.items.NpuItems;
import com.infinomat.minecraft.mod.npu.util.FileDataGetter;
import com.infinomat.minecraft.mod.npu.util.FolderDataGetter;
import com.infinomat.minecraft.mod.npu.util.PathTools;
import com.infinomat.minecraft.mod.npu.util.Reference;
import com.infinomat.minecraft.mod.npu.util.register.data.RegisterList;
import com.infinomat.minecraft.mod.npu.util.register.data.template.CreativeModeTabTemplate;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.net.URL;
import java.util.HashMap;
import java.util.List;


//用于注册新物品栏并向其中添加物品

public class NpuCreativeModeTabs {
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final String templateFolder = "template";
    public static final String registerFileName = "register.json";
    public static final String baseFolderPath = PathTools.linkPath(Reference.PATH.get(Reference.PathType.LOADER), Reference.PATH.get(Reference.PathType.CREATIVEMODETAB));
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Reference.MOD_ID);
    public static RegisterList registerList;
    private static final HashMap<String, CreativeModeTabRegister> TemplateMap = new HashMap<>();

    static {
        loadRegisterList();
        loadTemplate();
        register();
    }

    private static void loadRegisterList() {
        String filePath = PathTools.linkPath(baseFolderPath, registerFileName);
        URL url = FolderDataGetter.class.getClassLoader().getResource(filePath);
        try {
            if (url == null) throw new RuntimeException("Miss register.json");

            registerList = new FileDataGetter<>(filePath, RegisterList.class).getData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadTemplate() {
        String folderPath = PathTools.linkPath(baseFolderPath, templateFolder);

        for (CreativeModeTabTemplate CreativeModeTabTemplate : new FolderDataGetter<>(folderPath, CreativeModeTabTemplate.class).getList()) {
            TemplateMap.put(CreativeModeTabTemplate.getId(), CreativeModeTabRegister.create(CreativeModeTabTemplate));
        }
    }

    public static void register() {
        for (var group : registerList.getGroups()) {
            TemplateMap.get(group.template).register(group.items);
        }
    }
    private record CreativeModeTabRegister(int iconIndex) {
        public static CreativeModeTabRegister create(CreativeModeTabTemplate template_data) {
            return new CreativeModeTabRegister(template_data.getIconIndex());
        }

        public void register(List<String> ids) {
            for (var id : ids) {
                final String tabName = id.substring(0, id.length() - 4);
                registerCreativeModeTab(id, NpuItems.getItemList(tabName), NpuItems.getBlockItemList(tabName), iconIndex);
            }
        }
        private static void registerCreativeModeTab(String id, List<DeferredItem<Item>> itemList, List<DeferredItem<BlockItem>> blockItemList, int iconIndex) {
            final var CREATIVE_MODE_TAB = CREATIVE_MODE_TABS.register(id, () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativemodetab" + "." + Reference.MOD_ID + "." + id))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .displayItems((itemDisplayParameters, output) ->
                            {
                                //加物品
                                for (var ITEM : itemList)
                                    output.accept(ITEM.get());
                                for (var ITEM : blockItemList)
                                    output.accept(ITEM.get());
                            }
                    )
                    .icon(() -> {
                        if (iconIndex < itemList.size())
                            return new ItemStack(itemList.get(iconIndex).get());
                        else if (iconIndex < itemList.size() + blockItemList.size())
                            return new ItemStack(blockItemList.get(iconIndex - itemList.size()).get());
                        else
                            return new ItemStack(Items.BARRIER);
                    })
                    .build());

            LOGGER.info("Registered creative mode tab: {}", CREATIVE_MODE_TAB.getId());
        }
    }
}
