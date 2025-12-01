package com.infinomat.minecraft.mod.npu.events.handler;

import com.mojang.logging.LogUtils;
import com.infinomat.minecraft.mod.npu.entities.NpuEntities;
import com.infinomat.minecraft.mod.npu.util.Reference;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.slf4j.Logger;

@EventBusSubscriber(modid = Reference.MODID, value = Dist.CLIENT)
public class ClientEventHandler {
    public static final Logger LOGGER = LogUtils.getLogger();
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        LOGGER.info("Registering renderers...");

        NpuEntities.registerRenderers(event);
    }
}