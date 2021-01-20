package io.github.davidqf555.minecraft.manicworld.client.util;

import io.github.davidqf555.minecraft.manicworld.ManicWorld;
import io.github.davidqf555.minecraft.manicworld.client.render.NecromancerRenderer;
import io.github.davidqf555.minecraft.manicworld.client.render.PegasusRenderer;
import io.github.davidqf555.minecraft.manicworld.common.entities.EntityRegistry;
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
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.NECROMANCER_ENTITY.get(), NecromancerRenderer::new);
    }

}

