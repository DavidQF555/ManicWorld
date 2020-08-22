package com.davidqf.minecraft.manicworld.world.gen;

import com.davidqf.minecraft.manicworld.ManicWorld;
import com.davidqf.minecraft.manicworld.entities.EntityRegistry;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = ManicWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntitySpawnGen {

    @SubscribeEvent
    public static void spawnEntities(FMLLoadCompleteEvent event) {
        for (Biome biome : ForgeRegistries.BIOMES) {
            if (biome.equals(Biomes.SNOWY_MOUNTAINS) || biome.equals(Biomes.SNOWY_TAIGA_MOUNTAINS)) {
                biome.getSpawns(EntityClassification.CREATURE).add(new Biome.SpawnListEntry(EntityRegistry.PEGASUS_ENTITY.get(), 3, 1, 3));
            }
        }
    }
}
