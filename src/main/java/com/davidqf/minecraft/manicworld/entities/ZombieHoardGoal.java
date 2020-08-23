package com.davidqf.minecraft.manicworld.entities;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.*;

public class ZombieHoardGoal extends Goal {

    private static final int HOARD_CHECK = 32;
    private static final double HOARD_RANGE = 8;
    private final ZombieEntity zombie;
    private ZombieEntity target;

    public ZombieHoardGoal(ZombieEntity zombie) {
        super();
        this.zombie = zombie;
        target = null;
        setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean shouldExecute() {
        if (zombie.getNavigator().noPath()) {
            ZombieEntity best = null;
            List<ZombieEntity> nearby = zombie.world.getLoadedEntitiesWithinAABB(ZombieEntity.class, new AxisAlignedBB(zombie.getPosX() - HOARD_CHECK, zombie.getPosY() - HOARD_CHECK, zombie.getPosZ() - HOARD_CHECK, zombie.getPosX() + HOARD_CHECK, zombie.getPosY() + HOARD_CHECK, zombie.getPosZ() + HOARD_CHECK));
            if (nearby.size() > 1) {
                int maxAmt = 0;
                double minSq = Double.MAX_VALUE;
                for (ZombieEntity near : nearby) {
                    if (!near.equals(zombie) && zombie.getDistanceSq(near) > HOARD_RANGE * HOARD_RANGE && zombie.getNavigator().getPathToEntity(near, 1) != null) {
                        int amt = near.world.getLoadedEntitiesWithinAABB(ZombieEntity.class, new AxisAlignedBB(near.getPosX() - HOARD_RANGE, near.getPosY() - HOARD_RANGE, near.getPosZ() - HOARD_RANGE, near.getPosX() + HOARD_RANGE, near.getPosY() + HOARD_RANGE, near.getPosZ() + HOARD_RANGE)).size();
                        double distSq = near.getDistanceSq(zombie);
                        if ((amt == maxAmt && distSq < minSq) || amt > maxAmt) {
                            maxAmt = amt;
                            minSq = distSq;
                            best = near;
                        }
                    }
                }
                target = best;
                return best != null && zombie.getDistanceSq(target) > HOARD_RANGE * HOARD_RANGE;
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return target != null && !zombie.getNavigator().noPath() && zombie.getDistanceSq(target) > HOARD_RANGE * HOARD_RANGE;
    }

    @Override
    public void tick() {
        if (target != null) {
            zombie.getNavigator().tryMoveToEntityLiving(target, 1);
        }
    }

    @Override
    public void resetTask() {
        zombie.getNavigator().clearPath();
        target = null;
    }
}
