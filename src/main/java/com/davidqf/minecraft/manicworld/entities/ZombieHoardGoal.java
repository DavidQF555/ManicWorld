package com.davidqf.minecraft.manicworld.entities;

import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.*;

public class ZombieHoardGoal extends RandomWalkingGoal {

    private static final int HOARD_CHECK = 32;
    private static final double HOARD_RANGE = 4;
    private final ZombieEntity zombie;

    public ZombieHoardGoal(ZombieEntity zombie) {
        super(zombie, 1);
        this.zombie = zombie;
    }

    @Nullable
    @Override
    public Vector3d getPosition() {
        ZombieEntity best = null;
        List<ZombieEntity> nearby = zombie.world.getEntitiesWithinAABB(ZombieEntity.class, new AxisAlignedBB(zombie.getPosX() - HOARD_CHECK, zombie.getPosY() - HOARD_CHECK, zombie.getPosZ() - HOARD_CHECK, zombie.getPosX() + HOARD_CHECK, zombie.getPosY() + HOARD_CHECK, zombie.getPosZ() + HOARD_CHECK));
        if (nearby.size() > 1) {
            int maxAmt = 0;
            double minSq = Double.MAX_VALUE;
            for (ZombieEntity near : nearby) {
                if (!near.equals(zombie) && ((!zombie.isInWaterOrBubbleColumn() && near.getNavigator() instanceof GroundPathNavigator) || (zombie.isInWaterOrBubbleColumn() && near.getNavigator() instanceof SwimmerPathNavigator)) && zombie.getDistanceSq(near) > HOARD_RANGE * HOARD_RANGE && zombie.getNavigator().getPathToEntity(near, 1) != null) {
                    int amt = near.world.getEntitiesWithinAABB(ZombieEntity.class, new AxisAlignedBB(near.getPosX() - HOARD_RANGE, near.getPosY() - HOARD_RANGE, near.getPosZ() - HOARD_RANGE, near.getPosX() + HOARD_RANGE, near.getPosY() + HOARD_RANGE, near.getPosZ() + HOARD_RANGE)).size();
                    double distSq = near.getDistanceSq(zombie);
                    if ((amt == maxAmt && distSq < minSq) || amt > maxAmt) {
                        maxAmt = amt;
                        minSq = distSq;
                        best = near;
                    }
                }
            }
        }
        return best == null || zombie.getDistanceSq(best) <= HOARD_RANGE * HOARD_RANGE ? super.getPosition() : best.getPositionVec();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return super.shouldContinueExecuting() && zombie.getDistanceSq(x, y, z) > HOARD_RANGE * HOARD_RANGE;
    }
}
