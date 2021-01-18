package io.github.davidqf555.minecraft.manicworld.util;

import io.github.davidqf555.minecraft.manicworld.ManicWorld;
import io.github.davidqf555.minecraft.manicworld.entities.ZombieHoardGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ManicWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HoardingZombieEventHandler {

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof ZombieEntity) {
            ZombieEntity zombie = (ZombieEntity) event.getEntity();
            zombie.goalSelector.removeGoal(new WaterAvoidingRandomWalkingGoal(zombie, 1));
            zombie.goalSelector.removeGoal(new RandomWalkingGoal(zombie, 1));
            zombie.goalSelector.addGoal(7, new ZombieHoardGoal(zombie));
        }
    }
}
