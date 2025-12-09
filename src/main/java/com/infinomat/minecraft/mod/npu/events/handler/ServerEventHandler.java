package com.infinomat.minecraft.mod.npu.events.handler;

import com.infinomat.minecraft.mod.npu.util.Reference;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.DEDICATED_SERVER)
public class ServerEventHandler {
}