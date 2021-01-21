package io.github.davidqf555.minecraft.manicworld.common.entities;

import io.github.davidqf555.minecraft.manicworld.ManicWorld;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SpellcastingIllagerEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class NecromancerEntity extends SpellcastingIllagerEntity {

    private static final String TAG_KEY = ManicWorld.MOD_ID + ".necromancer_entity";
    private static final double MAX_DISTANCE = 16;
    private static final int SUMMON_RADIUS = 3;
    private static final int SPELL_COLOR = 0xFFAA00AA;
    private final List<UUID> summons;
    private int maxSummons;

    public NecromancerEntity(World worldIn) {
        super(EntityRegistry.NECROMANCER_ENTITY.get(), worldIn);
        experienceValue = 10;
        summons = new ArrayList<>();
        maxSummons = 2;
    }

    public static AttributeModifierMap.MutableAttribute setAttributes() {
        return MonsterEntity.func_234295_eP_()
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.5)
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 12)
                .createMutableAttribute(Attributes.MAX_HEALTH, 24);
    }

    @Override
    public void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new SwimGoal(this));
        goalSelector.addGoal(1, new SpellcastingIllagerEntity.CastingASpellGoal());
        goalSelector.addGoal(4, new RaiseDeadGoal());
        goalSelector.addGoal(8, new RandomWalkingGoal(this, 0.6));
        goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3, 1));
        goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8));
        targetSelector.addGoal(1, (new HurtByTargetGoal(this, AbstractRaiderEntity.class)).setCallsForHelp());
        targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true)).setUnseenMemoryTicks(300));
        targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, false)).setUnseenMemoryTicks(300));
        targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, false)).setUnseenMemoryTicks(300));
    }

    @Override
    public void livingTick() {
        if (world instanceof ServerWorld) {
            LivingEntity target = getAttackTarget();
            for (int i = summons.size() - 1; i >= 0; i--) {
                UUID id = summons.get(i);
                Entity summon = ((ServerWorld) world).getEntityByUuid(id);
                if (summon == null || !summon.isAlive() || getDistanceSq(summon) >= MAX_DISTANCE * MAX_DISTANCE) {
                    summons.remove(i);
                } else if (target != null && summon instanceof MobEntity) {
                    ((MobEntity) summon).setAttackTarget(target);
                }
            }
        }
        super.livingTick();
    }

    @Override
    public void tick() {
        if (world.isRemote() && isSpellcasting()) {
            SpellType type = getSpellType();
            setSpellType(SpellType.NONE);
            super.tick();
            setSpellType(type);
            float red = ColorHelper.PackedColor.getRed(SPELL_COLOR) / 255f;
            float blue = ColorHelper.PackedColor.getBlue(SPELL_COLOR) / 255f;
            float green = ColorHelper.PackedColor.getGreen(SPELL_COLOR) / 255f;
            float dir = (float) (renderYawOffset * Math.PI / 180) + MathHelper.cos(ticksExisted * 0.6662f) * 0.25f;
            float x = MathHelper.cos(dir);
            float y = MathHelper.sin(dir);
            world.addParticle(ParticleTypes.ENTITY_EFFECT, getPosX() + x * 0.6, getPosY() + 1.8, getPosZ() + y * 0.6, red, green, blue);
            world.addParticle(ParticleTypes.ENTITY_EFFECT, getPosX() - x * 0.6, getPosY() + 1.8, getPosZ() - y * 0.6, red, green, blue);

        } else {
            super.tick();
        }
    }

    @Override
    public SoundEvent getSpellSound() {
        return SoundEvents.ENTITY_EVOKER_CAST_SPELL;
    }

    @Override
    public void applyWaveBonus(int wave, boolean flag) {
        maxSummons += wave;
    }

    @Override
    public SoundEvent getRaidLossSound() {
        return SoundEvents.ENTITY_EVOKER_CELEBRATE;
    }

    @Override
    public SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_EVOKER_AMBIENT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_EVOKER_DEATH;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_EVOKER_HURT;
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        CompoundNBT nbt = (CompoundNBT) compound.get(TAG_KEY);
        if (nbt != null) {
            int size = nbt.getInt("Size");
            for (int i = 0; i < size; i++) {
                summons.add(nbt.getUniqueId("Summon" + i));
            }
        }
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("Size", summons.size());
        for (int i = 0; i < summons.size(); i++) {
            nbt.putUniqueId("Summon" + i, summons.get(i));
        }
        compound.put(TAG_KEY, nbt);
    }

    public static class Factory implements EntityType.IFactory<NecromancerEntity> {

        @Override
        public NecromancerEntity create(@Nullable EntityType<NecromancerEntity> type, World world) {
            return new NecromancerEntity(world);
        }
    }

    private class RaiseDeadGoal extends SpellcastingIllagerEntity.UseSpellGoal {

        private ZombieEntity summon;

        private RaiseDeadGoal() {
            summon = null;
        }

        @Override
        public boolean shouldExecute() {
            if (summons.size() < maxSummons && super.shouldExecute()) {
                if (world.isDaytime() && !world.isRaining()) {
                    summon = EntityType.HUSK.create(world);
                } else {
                    summon = EntityType.ZOMBIE.create(world);
                }
                if (summon != null) {
                    LivingEntity target = getAttackTarget();
                    summon.setAttackTarget(target);
                    List<BlockPos> locations = new ArrayList<>();
                    BlockPos center = target == null ? getPosition() : target.getPosition();
                    for (int x = -SUMMON_RADIUS; x <= SUMMON_RADIUS; x++) {
                        for (int y = -SUMMON_RADIUS; y <= SUMMON_RADIUS; y++) {
                            z:
                            for (int z = -SUMMON_RADIUS; z <= SUMMON_RADIUS; z++) {
                                BlockPos pos = center.add(x, y, z);
                                BlockPos down = pos.down();
                                if (getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) <= SUMMON_RADIUS * SUMMON_RADIUS && world.isTopSolid(down, summon) && !world.isTopSolid(pos, summon)) {
                                    for (int i = 1; i < (int) summon.getHeight(); i++) {
                                        if (!world.isAirBlock(pos.add(0, i, 0))) {
                                            continue z;
                                        }
                                    }
                                    locations.add(pos);
                                }
                            }
                        }
                    }
                    if (locations.size() > 0) {
                        BlockPos pos = locations.get(rand.nextInt(locations.size()));
                        summon.moveToBlockPosAndAngles(pos, 0, 0);
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void tick() {
            super.tick();
            BlockPos down = summon.getPosition().down();
            world.playEvent(Constants.WorldEvents.BREAK_BLOCK_EFFECTS, down, Block.getStateId(world.getBlockState(down)));
        }

        @Override
        public void castSpell() {
            if (world instanceof ServerWorld) {
                summon.setAttackTarget(getAttackTarget());
                summon.onInitialSpawn(((ServerWorld) world), world.getDifficultyForLocation(summon.getPosition()), SpawnReason.MOB_SUMMONED, null, null);
                ((ServerWorld) world).func_242417_l(summon);
                UUID id = summon.getUniqueID();
                summons.add(id);
            }
        }

        @Override
        public int getCastingTime() {
            return 40;
        }

        @Override
        public int getCastingInterval() {
            return 10;
        }

        @Nullable
        @Override
        public SoundEvent getSpellPrepareSound() {
            return SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON;
        }

        @Override
        public SpellType getSpellType() {
            return SpellType.SUMMON_VEX;
        }
    }
}
