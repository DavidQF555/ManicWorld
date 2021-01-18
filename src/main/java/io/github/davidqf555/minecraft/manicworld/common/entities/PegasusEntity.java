package io.github.davidqf555.minecraft.manicworld.common.entities;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PegasusEntity extends AbstractHorseEntity implements IFlyingAnimal {

    private static final IParticleData PARTICLE = ParticleTypes.END_ROD;
    private static final double TAME_CHANCE = 0.01;
    private static final Ingredient BREEDING = Ingredient.fromItems(Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE);

    public PegasusEntity(World worldIn) {
        super(EntityRegistry.PEGASUS_ENTITY.get(), worldIn);
        moveController = new FlyingMovementController(this, 60, true);
        setNoGravity(true);
    }

    public static AttributeModifierMap.MutableAttribute setAttributes() {
        return MobEntity.func_233666_p_()
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 1)
                .createMutableAttribute(Attributes.FLYING_SPEED, 1.5)
                .createMutableAttribute(Attributes.MAX_HEALTH, 20)
                .createMutableAttribute(Attributes.HORSE_JUMP_STRENGTH, 10);
    }

    @Override
    public void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new FlyAwayGoal(1.5));
    }

    @Override
    public PathNavigator createNavigator(World worldIn) {
        FlyingPathNavigator nav = new FlyingPathNavigator(this, worldIn);
        nav.setCanSwim(true);
        nav.setCanOpenDoors(false);
        nav.setCanEnterDoors(true);
        return nav;
    }

    @Override
    public void livingTick() {
        super.livingTick();
        float scale = getRenderScale();
        double x = this.rand.nextGaussian() * 0.02;
        double y = this.rand.nextGaussian() * 0.02;
        double z = this.rand.nextGaussian() * 0.02;
        world.addParticle(PARTICLE, getPosXRandom(scale), getPosYRandom() + scale / 2, getPosZRandom(scale), x, y, z);
    }

    @Override
    public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        if (!isChild()) {
            if (isTame() && player.isSecondaryUseActive()) {
                openGUI(player);
                return ActionResultType.func_233537_a_(world.isRemote);
            }
            if (isBeingRidden()) {
                return super.func_230254_b_(player, hand);
            }
        }
        if (!itemstack.isEmpty()) {
            if (isBreedingItem(itemstack)) {
                return func_241395_b_(player, itemstack);
            }
            ActionResultType actionresulttype = itemstack.interactWithEntity(player, this, hand);
            if (actionresulttype.isSuccessOrConsume()) {
                return actionresulttype;
            }
            if (!isTame()) {
                makeMad();
                return ActionResultType.func_233537_a_(world.isRemote);
            }
            boolean flag = !isChild() && !isHorseSaddled() && itemstack.getItem() == Items.SADDLE;
            if (flag) {
                openGUI(player);
                return ActionResultType.func_233537_a_(world.isRemote);
            }
        }
        if (isChild()) {
            return super.func_230254_b_(player, hand);
        } else {
            mountTo(player);
            return ActionResultType.func_233537_a_(world.isRemote);
        }
    }

    @Override
    public void travel(Vector3d vec) {
        if (isAlive()) {
            if (isBeingRidden() && canBeSteered() && isHorseSaddled()) {
                LivingEntity livingentity = (LivingEntity) getControllingPassenger();
                rotationYaw = livingentity.rotationYaw;
                prevRotationYaw = rotationYaw;
                rotationPitch = livingentity.rotationPitch * 0.5F;
                setRotation(rotationYaw, rotationPitch);
                renderYawOffset = rotationYaw;
                rotationYawHead = renderYawOffset;
                float forward = livingentity.moveForward;
                if (forward <= 0.0F) {
                    forward *= 0.25F;
                    gallopTime = 0;
                }
                if (jumpPower > 0.0F && !isHorseJumping() && onGround) {
                    double d0 = getHorseJumpStrength() * (double) jumpPower * (double) getJumpFactor();
                    double d1;
                    if (isPotionActive(Effects.JUMP_BOOST)) {
                        d1 = d0 + (double) ((float) (getActivePotionEffect(Effects.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
                    } else {
                        d1 = d0;
                    }
                    Vector3d vector3d = getMotion();
                    setMotion(vector3d.x, d1, vector3d.z);
                    setHorseJumping(true);
                    isAirBorne = true;
                    ForgeHooks.onLivingJump(this);
                    if (forward > 0.0F) {
                        float f2 = MathHelper.sin(rotationYaw * ((float) Math.PI / 180F));
                        float f3 = MathHelper.cos(rotationYaw * ((float) Math.PI / 180F));
                        setMotion(getMotion().add(-0.4F * f2 * jumpPower, 0.0D, 0.4F * f3 * jumpPower));
                    }
                    jumpPower = 0.0F;
                }
                jumpMovementFactor = getAIMoveSpeed() * 0.1F;
                if (canPassengerSteer()) {
                    setAIMoveSpeed((float) getAttributeValue(Attributes.FLYING_SPEED));
                    float pitch = -livingentity.getPitch(0) * 3.14159265358979323846f / 180;
                    super.travel(new Vector3d(livingentity.moveStrafing * 0.5, MathHelper.sin(pitch) * MathHelper.abs(forward), MathHelper.cos(pitch) * forward));
                } else if (livingentity instanceof PlayerEntity) {
                    setMotion(Vector3d.ZERO);
                }
                if (onGround) {
                    jumpPower = 0.0F;
                    setHorseJumping(false);
                }
                func_233629_a_(this, false);
            } else {
                jumpMovementFactor = 0.02F;
                super.travel(vec);
            }
        }
    }

    @Override
    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    @Override
    public void updateFallState(double y, boolean onGroundIn, @Nullable BlockState state, @Nullable BlockPos pos) {
    }

    @Override
    public boolean isOnLadder() {
        return false;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return BREEDING.test(stack);
    }

    @Override
    public boolean canMateWith(AnimalEntity otherAnimal) {
        return otherAnimal != this && canMate() && otherAnimal instanceof PegasusEntity && ((PegasusEntity) otherAnimal).canMate();
    }

    @Override
    public AgeableEntity func_241840_a(ServerWorld world, AgeableEntity mate) {
        PegasusEntity child = EntityRegistry.PEGASUS_ENTITY.get().create(world);
        setOffspringAttributes(mate, child);
        return child;
    }

    @Override
    public void playGallopSound(SoundType type) {
        super.playGallopSound(type);
        if (this.rand.nextInt(10) == 0) {
            this.playSound(SoundEvents.ENTITY_HORSE_BREATHE, type.getVolume() * 0.6F, type.getPitch());
        }
    }

    @Override
    public SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return SoundEvents.ENTITY_HORSE_AMBIENT;
    }

    @Override
    public SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.ENTITY_HORSE_DEATH;
    }

    @Nullable
    public SoundEvent func_230274_fe_() {
        return SoundEvents.ENTITY_HORSE_EAT;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource damageSourceIn) {
        super.getHurtSound(damageSourceIn);
        return SoundEvents.ENTITY_HORSE_HURT;
    }

    @Override
    public SoundEvent getAngrySound() {
        super.getAngrySound();
        return SoundEvents.ENTITY_HORSE_ANGRY;
    }

    public static class Factory implements EntityType.IFactory<PegasusEntity> {

        @Override
        public PegasusEntity create(@Nullable EntityType<PegasusEntity> type, World world) {
            return new PegasusEntity(world);
        }
    }

    private class FlyAwayGoal extends Goal {

        private static final int XZ = 64;
        private static final int Y = 64;
        private static final int MIN_DIST = 16;
        private final double speed;
        private BlockPos target;

        public FlyAwayGoal(double speedIn) {
            speed = speedIn;
            setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean shouldExecute() {
            if (!isTame() && isBeingRidden()) {
                List<BlockPos> pos = new ArrayList<>();
                for (int dX = -XZ; dX <= XZ; dX++) {
                    for (int dY = -Y; dY <= Y; dY++) {
                        for (int dZ = -XZ; dZ <= XZ; dZ++) {
                            BlockPos b = new BlockPos(getPosX() + dX, getPosY() + dY, getPosZ() + dZ);
                            BlockState state = world.getBlockState(b);
                            if (state.getBlock().isAir(state, world, b) && b.withinDistance(getPositionVec(), MIN_DIST)) {
                                pos.add(b);
                            }
                        }
                    }
                }
                if (pos.isEmpty()) {
                    return false;
                }
                target = pos.get(getRNG().nextInt(pos.size()));
                return true;
            }
            return false;
        }

        @Override
        public void startExecuting() {
            getNavigator().tryMoveToXYZ(target.getX(), target.getY(), target.getZ(), speed);
        }

        @Override
        public boolean shouldContinueExecuting() {
            return !isTame() && !getNavigator().noPath() && isBeingRidden();
        }

        @Override
        public void tick() {
            if (!isTame() && getRNG().nextDouble() < TAME_CHANCE) {
                Entity entity = getPassengers().get(0);
                if (entity == null) {
                    return;
                }
                if (entity instanceof PlayerEntity) {
                    int i = getTemper();
                    int j = getMaxTemper();
                    if (j > 0 && getRNG().nextInt(j) < i && !ForgeEventFactory.onAnimalTame(PegasusEntity.this, (PlayerEntity) entity)) {
                        setTamedBy((PlayerEntity) entity);
                        return;
                    }
                    increaseTemper(5);
                }
                removePassengers();
                makeMad();
                world.setEntityState(PegasusEntity.this, (byte) 6);
            }
        }
    }
}
