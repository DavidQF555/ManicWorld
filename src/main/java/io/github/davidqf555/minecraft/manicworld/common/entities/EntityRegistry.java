package io.github.davidqf555.minecraft.manicworld.common.entities;

import io.github.davidqf555.minecraft.manicworld.ManicWorld;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityRegistry {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, ManicWorld.MOD_ID);

    public static final RegistryObject<EntityType<PegasusEntity>> PEGASUS_ENTITY = ENTITY_TYPES.register("pegasus_entity", () -> EntityType.Builder.create(new PegasusEntity.Factory(), EntityClassification.CREATURE).size(1.3964f, 1.6f).build(new ResourceLocation(ManicWorld.MOD_ID, "pegasus_entity").toString()));

    public static void register() {
        ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}
