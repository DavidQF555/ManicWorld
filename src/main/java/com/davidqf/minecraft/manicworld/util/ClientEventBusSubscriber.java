package com.davidqf.minecraft.manicworld.util;

import com.davidqf.minecraft.manicworld.ManicWorld;
import com.davidqf.minecraft.manicworld.client.render.PegasusRenderer;
import com.davidqf.minecraft.manicworld.entities.EntityRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ManicWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.PEGASUS_ENTITY.get(), PegasusRenderer::new);
    }

}

