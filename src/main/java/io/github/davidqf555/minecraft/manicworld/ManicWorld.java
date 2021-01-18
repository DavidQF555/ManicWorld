package io.github.davidqf555.minecraft.manicworld;

import io.github.davidqf555.minecraft.manicworld.common.entities.EntityRegistry;
import io.github.davidqf555.minecraft.manicworld.common.entities.PegasusEntity;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("manicworld")
public class ManicWorld {

    public static final String MOD_ID = "manicworld";

    public ManicWorld() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        EntityRegistry.register();

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Deprecated
    private void setup(final FMLCommonSetupEvent event) {
        DeferredWorkQueue.runLater(() -> GlobalEntityTypeAttributes.put(EntityRegistry.PEGASUS_ENTITY.get(), PegasusEntity.setAttributes().create()));
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
    }

}
