package com.infinomat.minecraft.mod.npu.events.handler;

import com.infinomat.minecraft.mod.npu.entities.NpuEntities;
import com.infinomat.minecraft.mod.npu.util.Reference;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;


@EventBusSubscriber(modid = Reference.MODID)
public class BothSidesEventHandler {
    @SubscribeEvent
    public static void registerEntityAttributes(net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent event) {
        NpuEntities.registerAttributes(event);
    }
}
