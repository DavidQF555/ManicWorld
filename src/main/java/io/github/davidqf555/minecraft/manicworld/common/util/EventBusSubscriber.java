package io.github.davidqf555.minecraft.manicworld.common.util;

import io.github.davidqf555.minecraft.manicworld.ManicWorld;
import io.github.davidqf555.minecraft.manicworld.common.entities.EntityRegistry;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.raid.Raid;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class EventBusSubscriber {

    @Mod.EventBusSubscriber(modid = ManicWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeBus {

        @SubscribeEvent
        public static void onBiomeLoading(BiomeLoadingEvent event) {
            RegistryKey<Biome> key = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, event.getName());
            if (key.equals(Biomes.SNOWY_MOUNTAINS) || key.equals(Biomes.SNOWY_TAIGA_MOUNTAINS)) {
                event.getSpawns().withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityRegistry.PEGASUS_ENTITY.get(), 3, 1, 3));
            }
        }
    }

    @Mod.EventBusSubscriber(modid = ManicWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBus {

        @SubscribeEvent
        public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
            Raid.WaveMember.create(ManicWorld.MOD_ID + ":necromancer_entity", EntityRegistry.NECROMANCER_ENTITY.get(), new int[]{0, 0, 0, 0, 0, 1, 1, 2});
        }
    }
}
