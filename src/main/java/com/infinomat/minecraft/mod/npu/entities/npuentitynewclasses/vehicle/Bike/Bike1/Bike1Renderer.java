package com.infinomat.minecraft.mod.npu.entities.npuentitynewclasses.vehicle.Bike.Bike1;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

//Bike1的Renderer

public class Bike1Renderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<Bike1, R> {
    public Bike1Renderer(EntityRendererProvider.Context context) {
        super(context, new Bike1Model());
        this.shadowRadius = 0.5f;
    }
}
