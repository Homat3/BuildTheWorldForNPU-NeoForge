package com.infinomat.minecraft.mod.npu.blocks.npublocknewclasses.common;

import com.infinomat.minecraft.mod.npu.blocks.NpuBlocks;
import com.infinomat.minecraft.mod.npu.util.Reference;
import com.infinomat.minecraft.mod.npu.util.register.data.template.BlockTemplate;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockBehaviour;

public interface Common {
    static BlockBehaviour.Properties createBlockPropertiesOfMaterial(BlockTemplate template, String id) {
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.of();
        if (template.noLootTable) properties.noLootTable();
        if (template.noCollision) properties.noCollission();
        if (template.noOcclusion) properties.noOcclusion();
        if (template.noParticlesOnBreak) properties.noTerrainParticles();
        ResourceLocation LOCATION = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, id);
        return properties.setId(ResourceKey.create(ResourceKey.createRegistryKey(LOCATION), LOCATION));
    }
}
