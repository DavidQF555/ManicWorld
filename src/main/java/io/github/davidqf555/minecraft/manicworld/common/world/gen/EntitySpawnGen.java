package io.github.davidqf555.minecraft.manicworld.common.world.gen;

import io.github.davidqf555.minecraft.manicworld.ManicWorld;
import io.github.davidqf555.minecraft.manicworld.common.entities.EntityRegistry;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ManicWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntitySpawnGen {

    @SubscribeEvent
    public static void onBiomeLoading(BiomeLoadingEvent event) {
        RegistryKey<Biome> key = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, event.getName());
        if (key.equals(Biomes.SNOWY_MOUNTAINS) || key.equals(Biomes.SNOWY_TAIGA_MOUNTAINS)) {
            event.getSpawns().withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityRegistry.PEGASUS_ENTITY.get(), 3, 1, 3));
        }
    }
}
